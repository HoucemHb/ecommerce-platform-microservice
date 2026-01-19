package tn.fst.spring.ordersservice.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.sharedkernel.commands.*;
import tn.fst.spring.sharedkernel.events.*;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple Process Manager - In-Memory State (No Database)
 * Handles the complete order workflow: Stock → Payment → Confirmation
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ProcessingGroup("order-process-manager")
public class OrderManagementSaga {

    private final CommandGateway commandGateway;

    // In-memory state tracking
    private final Map<String, OrderProcessState> processStates = new ConcurrentHashMap<>();

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("=== ORDER CREATED EVENT ===");
        log.info("Order: {}", event.getOrderId());
        log.info("Customer: {}", event.getCustomerId());
        log.info("Amount: {}", event.getTotalAmount());
        log.info("Items: {}", event.getItems().size());

        // Create process state
        OrderProcessState state = new OrderProcessState(
                event.getOrderId(),
                event.getCustomerId(),
                event.getTotalAmount(),
                event.getItems().size()
        );

        processStates.put(event.getOrderId(), state);
        log.info("Process state created in memory");

        // Step 1: Reserve stock for each product
        event.getItems().forEach(item -> {
            log.info("Sending ReserveStockCommand for product: {}", item.getProductId());
            commandGateway.send(new ReserveStockCommand(
                    item.getProductId(),
                    event.getOrderId(),
                    item.getQuantity()
            ));
        });
    }

    @EventHandler
    public void on(StockReservedEvent event) {
        log.info("=== STOCK RESERVED EVENT ===");
        log.info("Product: {}, Order: {}", event.getProductId(), event.getOrderId());

        OrderProcessState state = processStates.get(event.getOrderId());

        if (state == null) {
            log.error("No process state found for order: {}", event.getOrderId());
            return;
        }

        log.info("Current state: orderId={}, customerId={}, amount={}",
                state.orderId, state.customerId, state.totalAmount);

        // Increment stock reservations
        state.completedStockReservations++;

        log.info("Stock reservations: {}/{}",
                state.completedStockReservations, state.expectedStockReservations);

        // Step 2: When all stock is reserved, validate payment
        if (state.completedStockReservations == state.expectedStockReservations) {
            log.info("ALL STOCK RESERVED! Proceeding to payment validation");

            String paymentId = UUID.randomUUID().toString();

            log.info("Sending ValidatePaymentCommand:");
            log.info("  - paymentId: {}", paymentId);
            log.info("  - orderId: {}", state.orderId);
            log.info("  - customerId: {}", state.customerId);
            log.info("  - totalAmount: {}", state.totalAmount);

            commandGateway.send(new ValidatePaymentCommand(
                    paymentId,
                    state.orderId,
                    state.customerId,
                    state.totalAmount,
                    "CREDIT_CARD"
            ));
        }
    }

    @EventHandler
    public void on(StockReservationFailedEvent event) {
        log.error("=== STOCK RESERVATION FAILED ===");
        log.error("Order: {}, Reason: {}", event.getOrderId(), event.getReason());

        // Cancel the order
        commandGateway.send(new CancelOrderCommand(
                event.getOrderId(),
                "Stock reservation failed: " + event.getReason()
        ));

        // Clean up state
        processStates.remove(event.getOrderId());
    }

    @EventHandler
    public void on(PaymentValidatedEvent event) {
        log.info("=== PAYMENT VALIDATED ===");
        log.info("Payment: {}, Order: {}", event.getPaymentId(), event.getOrderId());

        // Step 3: Confirm the order
        log.info("Sending ConfirmOrderCommand for order: {}", event.getOrderId());
        commandGateway.send(new ConfirmOrderCommand(event.getOrderId()));
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.error("=== PAYMENT FAILED ===");
        log.error("Order: {}, Reason: {}", event.getOrderId(), event.getReason());

        // Cancel the order
        commandGateway.send(new CancelOrderCommand(
                event.getOrderId(),
                "Payment failed: " + event.getReason()
        ));

        // Clean up state
        processStates.remove(event.getOrderId());
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        log.info("=== ORDER CONFIRMED - WORKFLOW COMPLETE ===");
        log.info("Order: {}", event.getOrderId());

        // Clean up state
        processStates.remove(event.getOrderId());
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("=== ORDER CANCELLED ===");
        log.info("Order: {}, Reason: {}", event.getOrderId(), event.getReason());

        // Clean up state
        processStates.remove(event.getOrderId());
    }

    /**
     * Simple inner class to track order process state
     */
    private static class OrderProcessState {
        String orderId;
        String customerId;
        Money totalAmount;
        int expectedStockReservations;
        int completedStockReservations = 0;

        OrderProcessState(String orderId, String customerId, Money totalAmount, int expectedStockReservations) {
            this.orderId = orderId;
            this.customerId = customerId;
            this.totalAmount = totalAmount;
            this.expectedStockReservations = expectedStockReservations;
        }
    }
}