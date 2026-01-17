package tn.fst.spring.paymentsservice.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.paymentsservice.query.models.PaymentReadModel;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentReadModel, String> {
    Optional<PaymentReadModel> findByOrderId(String orderId);
    List<PaymentReadModel> findByCustomerId(String customerId);
    List<PaymentReadModel> findByStatus(PaymentReadModel.PaymentStatus status);
}