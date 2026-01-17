package tn.fst.spring.paymentsservice.eventhandlers;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.sharedkernel.events.StockReservedEvent;

@Component
@Slf4j
public class PaymentValidationHandler {

    @EventHandler
    public void on(StockReservedEvent event) {
        log.info("Received StockReservedEvent for orderId: {}. Payment validation will be triggered by saga.",
                event.getOrderId());
        // The saga handles payment validation, so this is just for logging/monitoring
    }
}