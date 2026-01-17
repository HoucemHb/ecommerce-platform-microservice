package tn.fst.spring.productservice.command.aggregate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import tn.fst.spring.sharedkernel.commands.CreateProductCommand;
import tn.fst.spring.sharedkernel.commands.ReleaseStockCommand;
import tn.fst.spring.sharedkernel.commands.ReserveStockCommand;
import tn.fst.spring.sharedkernel.commands.UpdateStockCommand;
import tn.fst.spring.sharedkernel.events.*;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Aggregate
@NoArgsConstructor
@Slf4j
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String name;
    private String description;
    private Money price;
    private int availableStock;
    private Map<String, Integer> reservations; // orderId -> quantity

    @CommandHandler
    public ProductAggregate(CreateProductCommand command) {
        log.info("Handling CreateProductCommand for productId: {}", command.getProductId());

        // Validation
        if (command.getInitialStock() < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }

        AggregateLifecycle.apply(new ProductCreatedEvent(
                command.getProductId(),
                command.getName(),
                command.getDescription(),
                command.getPrice(),
                command.getInitialStock()
        ));
    }

    @CommandHandler
    public void handle(ReserveStockCommand command) {
        log.info("Handling ReserveStockCommand for productId: {} orderId: {} quantity: {}",
                command.getProductId(), command.getOrderId(), command.getQuantity());

        // Check if stock is available
        if (availableStock < command.getQuantity()) {
            log.error("Insufficient stock. Available: {}, Requested: {}",
                    availableStock, command.getQuantity());

            AggregateLifecycle.apply(new StockReservationFailedEvent(
                    command.getProductId(),
                    command.getOrderId(),
                    command.getQuantity(),
                    availableStock,
                    "Insufficient stock available"
            ));
            return;
        }

        AggregateLifecycle.apply(new StockReservedEvent(
                command.getProductId(),
                command.getOrderId(),
                command.getQuantity(),
                availableStock - command.getQuantity(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(ReleaseStockCommand command) {
        log.info("Handling ReleaseStockCommand for productId: {} orderId: {}",
                command.getProductId(), command.getOrderId());

        // Check if there's a reservation for this order
        if (!reservations.containsKey(command.getOrderId())) {
            log.warn("No reservation found for orderId: {}", command.getOrderId());
            return;
        }

        AggregateLifecycle.apply(new StockReleasedEvent(
                command.getProductId(),
                command.getOrderId(),
                command.getQuantity(),
                Instant.now()
        ));
    }

    @CommandHandler
    public void handle(UpdateStockCommand command) {
        log.info("Handling UpdateStockCommand for productId: {} new quantity: {}",
                command.getProductId(), command.getQuantity());

        if (command.getQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        AggregateLifecycle.apply(new StockUpdatedEvent(
                command.getProductId(),
                availableStock,
                command.getQuantity()
        ));
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.price = event.getPrice();
        this.availableStock = event.getInitialStock();
        this.reservations = new HashMap<>();
        log.info("Product created: {} with stock: {}", productId, availableStock);
    }

    @EventSourcingHandler
    public void on(StockReservedEvent event) {
        this.availableStock = event.getRemainingStock();
        this.reservations.put(event.getOrderId(), event.getQuantity());
        log.info("Stock reserved: {} units for order: {}. Remaining: {}",
                event.getQuantity(), event.getOrderId(), availableStock);
    }

    @EventSourcingHandler
    public void on(StockReleasedEvent event) {
        Integer reservedQuantity = reservations.remove(event.getOrderId());
        if (reservedQuantity != null) {
            this.availableStock += reservedQuantity;
            log.info("Stock released: {} units from order: {}. New available: {}",
                    reservedQuantity, event.getOrderId(), availableStock);
        }
    }

    @EventSourcingHandler
    public void on(StockUpdatedEvent event) {
        this.availableStock = event.getNewStock();
        log.info("Stock updated from {} to {}", event.getOldStock(), event.getNewStock());
    }
}
