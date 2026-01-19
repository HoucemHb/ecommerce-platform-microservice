package tn.fst.spring.ordersservice.command.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.ordersservice.command.api.requests.CreateOrderRequest;
import tn.fst.spring.sharedkernel.commands.CancelOrderCommand;
import tn.fst.spring.sharedkernel.commands.ConfirmOrderCommand;
import tn.fst.spring.sharedkernel.commands.CreateOrderCommand;
import tn.fst.spring.sharedkernel.valueobjects.Address;
import tn.fst.spring.sharedkernel.valueobjects.Money;
import tn.fst.spring.sharedkernel.valueobjects.OrderLineItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createOrder(@RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();

        List<OrderLineItem> items = request.getItems().stream()
                .map(item -> new OrderLineItem(
                        item.getProductId(),
                        item.getQuantity(),
                        Money.of(item.getUnitPrice(), "USD")
                ))
                .collect(Collectors.toList());

        CreateOrderCommand command = new CreateOrderCommand(
                orderId,
                request.getCustomerId(),
                items,
                new Address(
                        request.getShippingAddress().getStreet(),
                        request.getShippingAddress().getCity(),
                        request.getShippingAddress().getZipCode(),
                        request.getShippingAddress().getCountry()
                ),
                Money.of(new BigDecimal(5000), "USD")
        );

        return commandGateway.send(command)
                .thenApply(result -> ResponseEntity.ok(orderId));
    }

    @PostMapping("/{orderId}/confirm")
    public CompletableFuture<ResponseEntity<Void>> confirmOrder(@PathVariable String orderId) {
        return commandGateway.send(new ConfirmOrderCommand(orderId))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }

    @PostMapping("/{orderId}/cancel")
    public CompletableFuture<ResponseEntity<Void>> cancelOrder(
            @PathVariable String orderId,
            @RequestParam String reason) {
        return commandGateway.send(new CancelOrderCommand(orderId, reason))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }
}

