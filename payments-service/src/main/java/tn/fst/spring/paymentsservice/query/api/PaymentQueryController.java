package tn.fst.spring.paymentsservice.query.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.paymentsservice.query.models.PaymentReadModel;
import tn.fst.spring.paymentsservice.query.repository.PaymentRepository;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentQueryController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentReadModel> getPayment(@PathVariable String paymentId) {
        return paymentRepository.findById(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentReadModel> getPaymentByOrder(@PathVariable String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public List<PaymentReadModel> getPaymentsByCustomer(@PathVariable String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    @GetMapping
    public List<PaymentReadModel> getAllPayments() {
        return paymentRepository.findAll();
    }
}