package tn.fst.spring.ordersservice.saga;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import tn.fst.spring.sharedkernel.commands.*;
import tn.fst.spring.sharedkernel.events.*;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Axon Saga - Gère le workflow complet d'une commande : Stock → Paiement → Confirmation
 */
@Saga
@Slf4j
@ProcessingGroup("orderManagementSagaProcessor")
public class OrderManagementSaga {

    public OrderManagementSaga() {
        // REQUIRED by Axon for saga rehydration
    }

    private transient CommandGateway commandGateway;

    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private String currency;
    private int expectedStockReservations;
    private int completedStockReservations;


    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderCreatedEvent event) {
        if (commandGateway == null) {
            log.error("❌ CommandGateway IS NULL in OrderManagementSaga");
        }
        log.info("=== ORDER CREATED EVENT ===");
        log.info("OrderId: {}", event.getOrderId());
        log.info("CustomerId: {}", event.getCustomerId());
        log.info("TotalAmount: {}", event.getTotalAmount());
        log.info("Items size: {}", event.getItems() != null ? event.getItems().size() : 0);

        // Initialize saga state
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.totalAmount = event.getTotalAmount().getAmount();
        this.currency = event.getTotalAmount().getCurrency();
        this.expectedStockReservations = event.getItems().size();
        this.completedStockReservations = 0;


        log.info("Expected stock reservations: {}", this.expectedStockReservations);
        log.info("Saga started for order: {}", orderId);

        if (this.expectedStockReservations == 0) {
            log.warn("No items to reserve! Proceeding directly to payment.");
            proceedToPayment();
            return;
        }

        // Reserve stock for each product
        event.getItems().forEach(item -> {
            log.info("Sending ReserveStockCommand for product: {}", item.getProductId());
            commandGateway.send(new ReserveStockCommand(
                    item.getProductId(),
                    orderId,
                    item.getQuantity()
            ));
        });
    }
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(StockReservedEvent event) {
        if (commandGateway == null) {
            log.error("❌ CommandGateway IS NULL in OrderManagementSaga");
        }
        log.info("=== STOCK RESERVED EVENT ===");
        log.info("Product: {}, Order: {}", event.getProductId(), event.getOrderId());

        // Increment the counter
        completedStockReservations++;

        log.info("Completed stock reservations: {}/{}",
                completedStockReservations, expectedStockReservations);

        // Check if all reservations are complete
        if (completedStockReservations >= expectedStockReservations) {
            log.info("ALL STOCK RESERVED! Proceeding to payment validation");
//            proceedToPayment();
        }
    }

    private void proceedToPayment() {
        if (commandGateway == null) {
            log.error("❌ CommandGateway IS NULL in OrderManagementSaga");
        }
        String paymentId = UUID.randomUUID().toString();
        log.info("Sending ValidatePaymentCommand:");
        log.info("  paymentId: {}", paymentId);
        log.info("  orderId: {}", orderId);
        log.info("  customerId: {}", customerId);
        log.info("  totalAmount: {}", totalAmount);

        commandGateway.send(new ValidatePaymentCommand(
                paymentId,
                orderId,
                customerId,
                new Money(totalAmount, currency),
                "CREDIT_CARD"
        ));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(StockReservationFailedEvent event) {
        if (commandGateway == null) {
            log.error("❌ CommandGateway IS NULL in OrderManagementSaga");
        }
        log.error("=== STOCK RESERVATION FAILED ===");
        log.error("Order: {}, Reason: {}", event.getOrderId(), event.getReason());
        commandGateway.send(new CancelOrderCommand(
                event.getOrderId(),
                "Stock reservation failed: " + event.getReason()
        ));
    }

//    @SagaEventHandler(associationProperty = "orderId")
//    public void on(PaymentValidatedEvent event) {
//        log.info("=== PAYMENT VALIDATED ===");
//        log.info("Payment: {}, Order: {}", event.getPaymentId(), event.getOrderId());
//        log.info("Sending ConfirmOrderCommand for order: {}", event.getOrderId());
//        commandGateway.send(new ConfirmOrderCommand(event.getOrderId()));
//    }

//    @EndSaga
//    @SagaEventHandler(associationProperty = "orderId")
//    public void on(PaymentFailedEvent event) {
//        log.error("=== PAYMENT FAILED ===");
//        log.error("Order: {}, Reason: {}", event.getOrderId(), event.getReason());
//        commandGateway.send(new CancelOrderCommand(
//                event.getOrderId(),
//                "Payment failed: " + event.getReason()
//        ));
//    }

//    @EndSaga
//    @SagaEventHandler(associationProperty = "orderId")
//    public void on(OrderConfirmedEvent event) {
//        log.info("=== ORDER CONFIRMED - WORKFLOW COMPLETE ===");
//        log.info("Order: {}", event.getOrderId());
//    }
//
//    @EndSaga
//    @SagaEventHandler(associationProperty = "orderId")
//    public void on(OrderCancelledEvent event) {
//        log.info("=== ORDER CANCELLED ===");
//        log.info("Order: {}, Reason: {}", event.getOrderId(), event.getReason());
//    }
}