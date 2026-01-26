package tn.fst.spring.ordersservice.command.agregate;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import tn.fst.spring.sharedkernel.commands.CancelOrderCommand;
import tn.fst.spring.sharedkernel.commands.ConfirmOrderCommand;
import tn.fst.spring.sharedkernel.commands.CreateOrderCommand;
import tn.fst.spring.sharedkernel.events.OrderCancelledEvent;
import tn.fst.spring.sharedkernel.events.OrderConfirmedEvent;
import tn.fst.spring.sharedkernel.events.OrderCreatedEvent;
import tn.fst.spring.sharedkernel.valueobjects.Address;
import tn.fst.spring.sharedkernel.valueobjects.Money;
import tn.fst.spring.sharedkernel.valueobjects.OrderLineItem;

import java.time.Instant;
import java.util.List;

@Aggregate
@NoArgsConstructor
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String customerId;
    private List<OrderLineItem> items;
    private Address shippingAddress;
    private Money totalAmount;
    private OrderStatus status;

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand for orderId: {}", command.getOrderId());

        // Business validation
        if (command.getItems() == null || command.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        if (command.getTotalAmount().getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total must be positive");
        }

        // Emit event
        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(),
                command.getCustomerId(),
                command.getItems(),
                command.getShippingAddress(),
                command.getTotalAmount(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        log.info("Handling ConfirmOrderCommand for orderId: {}", command.getOrderId());

        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm order in status: " + status);
        }

        AggregateLifecycle.apply(new OrderConfirmedEvent(
                command.getOrderId(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        log.info("Handling CancelOrderCommand for orderId: {}", command.getOrderId());

        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel confirmed order");
        }

        AggregateLifecycle.apply(new OrderCancelledEvent(
                command.getOrderId(),
                command.getReason(),
                Instant.now(),
                this.items // <-- inclure les produits de la commande

        ));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.items = event.getItems();
        this.shippingAddress = event.getShippingAddress();
        this.totalAmount = event.getTotalAmount();
        this.status = OrderStatus.PENDING;
        log.info("Order created: {}", orderId);
    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.status = OrderStatus.CONFIRMED;
        log.info("Order confirmed: {}", orderId);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.status = OrderStatus.CANCELLED;
        log.info("Order cancelled: {}", orderId);
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}