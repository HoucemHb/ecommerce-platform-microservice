package tn.fst.spring.paymentsservice.command.aggregate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import tn.fst.spring.sharedkernel.commands.RefundPaymentCommand;
import tn.fst.spring.sharedkernel.commands.ValidatePaymentCommand;
import tn.fst.spring.sharedkernel.events.PaymentFailedEvent;
import tn.fst.spring.sharedkernel.events.PaymentRefundedEvent;
import tn.fst.spring.sharedkernel.events.PaymentValidatedEvent;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.time.Instant;
import java.util.Random;

@Aggregate
@NoArgsConstructor
@Slf4j
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;
    private String customerId;
    private Money amount;
    private PaymentStatus status;
    private String paymentMethod;

    @CommandHandler
    public PaymentAggregate(ValidatePaymentCommand command) {
        log.info("=== PAYMENT VALIDATION STARTED ===");
        log.info("PaymentId: {}", command.getPaymentId());
        log.info("OrderId: {}", command.getOrderId());
        log.info("CustomerId: {}", command.getCustomerId());
        log.info("TotalAmount: {}", command.getTotalAmount());
        log.info("PaymentMethod: {}", command.getPaymentMethod());

        // Additional debug logging
        if (command.getTotalAmount() != null && command.getTotalAmount().getAmount() != null) {
            log.info("Amount value: {} {}",
                    command.getTotalAmount().getAmount(),
                    command.getTotalAmount().getCurrency());
        }

        // CRITICAL FIX: Use getTotalAmount() not getAmount()
        if (command.getTotalAmount() == null) {
            log.error("TotalAmount is NULL!");
            AggregateLifecycle.apply(new PaymentFailedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getTotalAmount(),
                    "Payment amount is null",
                    Instant.now()
            ));
            return;
        }

        if (command.getTotalAmount().getAmount() == null) {
            log.error("TotalAmount.amount is NULL!");
            AggregateLifecycle.apply(new PaymentFailedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getTotalAmount(),
                    "Payment amount value is null",
                    Instant.now()
            ));
            return;
        }

        // Validation
        if (command.getTotalAmount().getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}", command.getTotalAmount().getAmount());
            AggregateLifecycle.apply(new PaymentFailedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getTotalAmount(),
                    "Payment amount must be positive",
                    Instant.now()
            ));
            return;
        }

        // Simulate payment gateway validation (80% success rate)
        boolean paymentSuccess = simulatePaymentGateway();

        if (paymentSuccess) {
            log.info("Payment validation SUCCESSFUL - applying PaymentValidatedEvent");
            AggregateLifecycle.apply(new PaymentValidatedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getCustomerId(),
                    command.getTotalAmount(),
                    command.getPaymentMethod(),
                    Instant.now()
            ));
        } else {
            log.error("Payment validation FAILED - simulated gateway rejection");
            AggregateLifecycle.apply(new PaymentFailedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getTotalAmount(),
                    "Payment gateway rejected the transaction (simulated)",
                    Instant.now()
            ));
        }
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        log.info("Handling RefundPaymentCommand for paymentId: {}", command.getPaymentId());

        if (status != PaymentStatus.VALIDATED) {
            throw new IllegalStateException("Can only refund validated payments");
        }

        AggregateLifecycle.apply(new PaymentRefundedEvent(
                command.getPaymentId(),
                command.getOrderId(),
                command.getAmount(),
                Instant.now()
        ));
    }

    @EventSourcingHandler
    public void on(PaymentValidatedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.customerId = event.getCustomerId();
        this.amount = event.getAmount();
        this.paymentMethod = event.getPaymentMethod();
        this.status = PaymentStatus.VALIDATED;
        log.info("Payment validated: {}", paymentId);
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.paymentId = event.getPaymentId();
        this.orderId = event.getOrderId();
        this.amount = event.getAmount();
        this.status = PaymentStatus.FAILED;
        log.info("Payment failed: {}", paymentId);
    }

    @EventSourcingHandler
    public void on(PaymentRefundedEvent event) {
        this.status = PaymentStatus.REFUNDED;
        log.info("Payment refunded: {}", paymentId);
    }

    private boolean simulatePaymentGateway() {
        // Simulate payment processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 80% success rate for demo purposes
        boolean success = new Random().nextDouble() < 0.8;
        log.info("Payment gateway simulation result: {}", success ? "SUCCESS" : "FAILED");
        return success;
    }

    public enum PaymentStatus {
        PENDING, VALIDATED, FAILED, REFUNDED
    }
}