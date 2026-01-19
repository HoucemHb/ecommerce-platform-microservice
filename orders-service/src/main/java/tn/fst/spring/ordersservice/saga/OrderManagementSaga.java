package tn.fst.spring.ordersservice.saga;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import tn.fst.spring.sharedkernel.commands.CancelOrderCommand;
import tn.fst.spring.sharedkernel.commands.ConfirmOrderCommand;
import tn.fst.spring.sharedkernel.commands.ReserveStockCommand;
import tn.fst.spring.sharedkernel.commands.ValidatePaymentCommand;
import tn.fst.spring.sharedkernel.events.*;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.UUID;

@Saga
@NoArgsConstructor
@Slf4j
public class OrderManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String customerId;
    private Money totalAmount;

    // Track stock reservations
    private int expectedStockReservations = 0;
    private int completedStockReservations = 0;

    private boolean paymentValidated = false;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("Saga started for order: {}", event.getOrderId());
        log.info("with Amount: {}", event.getTotalAmount());

        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.totalAmount = event.getTotalAmount();
        this.expectedStockReservations = event.getItems().size();

        log.info("Expecting {} stock reservations", expectedStockReservations);

        // Step 1: Reserve stock for each product
        event.getItems().forEach(item -> {
            log.info("Reserving stock for product: {}", item.getProductId());
            commandGateway.send(new ReserveStockCommand(
                    item.getProductId(),
                    event.getOrderId(),
                    item.getQuantity()
            ));
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(StockReservedEvent event) {
        log.info("Stock reserved for product {} in order: {}", event.getProductId(), event.getOrderId());
        log.info("Saga state - orderId: {}, customerId: {}, totalAmount: {}",
                this.orderId, this.customerId, this.totalAmount);

        // Verify the saga instance is correctly loaded
        if (this.orderId == null) {
            log.error("CRITICAL: Saga state not loaded correctly!");
            return;
        }

        completedStockReservations++;
        log.info("Stock reservations: {}/{}", completedStockReservations, expectedStockReservations);

        // Only proceed to payment validation when ALL stock is reserved
        if (completedStockReservations == expectedStockReservations) {
            log.info("All stock reserved, proceeding to payment validation");

            String paymentId = UUID.randomUUID().toString();
            SagaLifecycle.associateWith("paymentId", paymentId);

            commandGateway.send(new ValidatePaymentCommand(
                    paymentId,
                    this.orderId,
                    this.customerId,
                    this.totalAmount,
                    "CREDIT_CARD"
            ));
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(StockReservationFailedEvent event) {
        log.error("Stock reservation failed for order: {}", event.getOrderId());

        // Cancel the order immediately if any stock reservation fails
        commandGateway.send(new CancelOrderCommand(
                this.orderId,
                "Insufficient stock: " + event.getReason()
        ));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentValidatedEvent event) {
        log.info("Payment validated for order: {}", event.getOrderId());
        this.paymentValidated = true;

        // Step 3: Confirm the order
        commandGateway.send(new ConfirmOrderCommand(this.orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentFailedEvent event) {
        log.error("Payment failed for order: {}", event.getOrderId());

        // Cancel the order
        commandGateway.send(new CancelOrderCommand(
                this.orderId,
                "Payment failed: " + event.getReason()
        ));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderConfirmedEvent event) {
        log.info("Order confirmed, ending saga: {}", event.getOrderId());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCancelledEvent event) {
        log.info("Order cancelled, ending saga: {}", event.getOrderId());
    }
}