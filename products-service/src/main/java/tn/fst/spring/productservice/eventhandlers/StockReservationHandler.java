package tn.fst.spring.productservice.eventhandlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.sharedkernel.commands.ReleaseStockCommand;
import tn.fst.spring.sharedkernel.events.OrderCancelledEvent;
import tn.fst.spring.sharedkernel.events.PaymentFailedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReservationHandler {

    private final CommandGateway commandGateway;

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Received OrderCancelledEvent for orderId: {}. Releasing stock...", event.getOrderId());

        if (event.getItems() == null || event.getItems().isEmpty()) {
            log.warn("No items in cancelled order {}", event.getOrderId());
            return;
        }

        event.getItems().forEach(item -> {
            log.info("Releasing {} units of product {} for order {}",
                    item.getQuantity(), item.getProductId(), event.getOrderId());

            // Envoi de la commande ReleaseStockCommand pour chaque produit
            commandGateway.send(new ReleaseStockCommand(
                    item.getProductId(),
                    event.getOrderId(),
                    item.getQuantity()
            ));
        });
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent for orderId: {}. Releasing stock...",
                event.getOrderId());

        // Same as above - release stock for all products in the order
    }
}