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
        log.info("Handling ValidatePaymentCommand for paymentId: {}", command.getPaymentId());

        // Validation
        if (command.getAmount().getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Simulate payment gateway validation (80% success rate)
        boolean paymentSuccessful = simulatePaymentGateway();

        if (paymentSuccessful) {
            AggregateLifecycle.apply(new PaymentValidatedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getCustomerId(),
                    command.getAmount(),
                    command.getPaymentMethod(),
                    Instant.now()
            ));
        } else {
            AggregateLifecycle.apply(new PaymentFailedEvent(
                    command.getPaymentId(),
                    command.getOrderId(),
                    command.getAmount(),
                    "Payment gateway declined the transaction",
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
        return new Random().nextDouble() < 0.8;
    }

    public enum PaymentStatus {
        PENDING, VALIDATED, FAILED, REFUNDED
    }
}