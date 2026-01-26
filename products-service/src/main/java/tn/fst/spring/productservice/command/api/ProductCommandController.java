package tn.fst.spring.productservice.command.api;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.sharedkernel.commands.CreateProductCommand;
import tn.fst.spring.sharedkernel.commands.DeleteProductCommand;
import tn.fst.spring.sharedkernel.commands.ReserveStockCommand;
import tn.fst.spring.sharedkernel.commands.UpdateStockCommand;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createProduct(@RequestBody CreateProductRequest request) {
        String productId = UUID.randomUUID().toString();

        CreateProductCommand command = new CreateProductCommand(
                productId,
                request.getName(),
                request.getDescription(),
                Money.of(request.getPrice(), "USD"),
                request.getInitialStock()
        );

        return commandGateway.send(command)
                .thenApply(result -> ResponseEntity.ok(productId));
    }

    @PutMapping("/{productId}/stock")
    public CompletableFuture<ResponseEntity<Void>> updateStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        return commandGateway.send(new UpdateStockCommand(productId, quantity))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }

    @PostMapping("/{productId}/reserve")
    public CompletableFuture<ResponseEntity<Void>> reserveStock(
            @PathVariable String productId,
            @RequestParam String orderId,
            @RequestParam int quantity) {
        return commandGateway.send(new ReserveStockCommand(productId, orderId, quantity))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }
    @DeleteMapping("/{productId}")
    public CompletableFuture<ResponseEntity<Void>> deleteProduct(@PathVariable String productId) {
        return commandGateway.send(new DeleteProductCommand(productId))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }

}

@Data
class CreateProductRequest {
    private String name;
    private String description;
    private java.math.BigDecimal price;
    private int initialStock;
}