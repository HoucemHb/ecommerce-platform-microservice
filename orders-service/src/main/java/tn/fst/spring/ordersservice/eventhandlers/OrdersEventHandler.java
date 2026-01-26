package tn.fst.spring.ordersservice.eventhandlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.fst.spring.sharedkernel.commands.ConfirmOrderCommand;
import tn.fst.spring.sharedkernel.events.*;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrdersEventHandler {

    private final CommandGateway commandGateway; // injecté via RequiredArgsConstructor

    @EventHandler
    public void on(PaymentValidatedEvent event) {
        log.info("Handling PaymentValidatedEvent for orderId: {}", event.getOrderId());

        // On envoie une commande pour confirmer la commande
        commandGateway.send(new ConfirmOrderCommand(event.getOrderId()));

        log.info("ConfirmOrderCommand sent for orderId: {}", event.getOrderId());
    }

}
