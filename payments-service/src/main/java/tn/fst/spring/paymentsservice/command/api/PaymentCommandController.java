package tn.fst.spring.paymentsservice.command.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.sharedkernel.commands.RefundPaymentCommand;
import tn.fst.spring.sharedkernel.commands.ValidatePaymentCommand;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/validate")
    public CompletableFuture<ResponseEntity<String>> validatePayment(@RequestBody ValidatePaymentRequest request) {
        String paymentId = UUID.randomUUID().toString();

        ValidatePaymentCommand command = new ValidatePaymentCommand(
                paymentId,
                request.getOrderId(),
                request.getCustomerId(),
                Money.of(request.getAmount(), "USD"),
                request.getPaymentMethod()
        );

        return commandGateway.send(command)
                .thenApply(result -> ResponseEntity.ok(paymentId));
    }

    @PostMapping("/{paymentId}/refund")
    public CompletableFuture<ResponseEntity<Void>> refundPayment(
            @PathVariable String paymentId,
            @RequestBody RefundPaymentRequest request) {
        return commandGateway.send(new RefundPaymentCommand(
                        paymentId,
                        request.getOrderId(),
                        Money.of(request.getAmount(), "USD")
                ))
                .thenApply(result -> ResponseEntity.ok().<Void>build());
    }
}

@Data
class ValidatePaymentRequest {
    private String orderId;
    private String customerId;
    private java.math.BigDecimal amount;
    private String paymentMethod;
}

@Data
class RefundPaymentRequest {
    private String orderId;
    private java.math.BigDecimal amount;
}