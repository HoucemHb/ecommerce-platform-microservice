package tn.fst.spring.productservice.eventhandlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.sharedkernel.events.OrderCancelledEvent;
import tn.fst.spring.sharedkernel.events.PaymentFailedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReservationHandler {

    private final CommandGateway commandGateway;

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Received OrderCancelledEvent for orderId: {}. Releasing stock...",
                event.getOrderId());

        // In a real scenario, we'd need to know which products were in the order
        // This would typically come from a read model or be included in the event
        // For demonstration, we're showing the pattern

        // commandGateway.send(new ReleaseStockCommand(productId, event.getOrderId(), quantity));
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Received PaymentFailedEvent for orderId: {}. Releasing stock...",
                event.getOrderId());

        // Same as above - release stock for all products in the order
    }
}