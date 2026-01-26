package tn.fst.spring.paymentsservice.query.projections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.paymentsservice.query.models.PaymentReadModel;
import tn.fst.spring.paymentsservice.query.repository.PaymentRepository;
import tn.fst.spring.sharedkernel.events.PaymentFailedEvent;
import tn.fst.spring.sharedkernel.events.PaymentRefundedEvent;
import tn.fst.spring.sharedkernel.events.PaymentValidatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
@ProcessingGroup("payment-projections")  // ← THIS WAS MISSING!
public class PaymentProjection {

    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentValidatedEvent event) {
        log.info("Projecting PaymentValidatedEvent for paymentId: {}", event.getPaymentId());

        PaymentReadModel payment = new PaymentReadModel();
        payment.setPaymentId(event.getPaymentId());
        payment.setOrderId(event.getOrderId());
        payment.setCustomerId(event.getCustomerId());
        payment.setStatus(PaymentReadModel.PaymentStatus.VALIDATED);
        payment.setAmount(event.getAmount().getAmount());
        payment.setCurrency(event.getAmount().getCurrency());
        payment.setPaymentMethod(event.getPaymentMethod());
        payment.setValidatedAt(event.getValidatedAt());

        paymentRepository.save(payment);
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Projecting PaymentFailedEvent for paymentId: {}", event.getPaymentId());

        PaymentReadModel payment = new PaymentReadModel();
        payment.setPaymentId(event.getPaymentId());
        payment.setOrderId(event.getOrderId());
        payment.setStatus(PaymentReadModel.PaymentStatus.FAILED);
        payment.setAmount(event.getAmount().getAmount());
        payment.setCurrency(event.getAmount().getCurrency());
        payment.setFailureReason(event.getReason());
        payment.setFailedAt(event.getFailedAt());

        paymentRepository.save(payment);
    }

    @EventHandler
    public void on(PaymentRefundedEvent event) {
        log.info("Projecting PaymentRefundedEvent for paymentId: {}", event.getPaymentId());

        paymentRepository.findById(event.getPaymentId()).ifPresent(payment -> {
            payment.setStatus(PaymentReadModel.PaymentStatus.REFUNDED);
            payment.setRefundedAt(event.getRefundedAt());
            paymentRepository.save(payment);
        });
    }

    @ResetHandler
    public void reset() {
        log.warn("Resetting Payment projection - deleting all read models");
        paymentRepository.deleteAll();
    }
}

