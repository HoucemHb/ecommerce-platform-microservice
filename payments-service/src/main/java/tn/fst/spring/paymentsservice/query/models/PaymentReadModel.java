package tn.fst.spring.paymentsservice.query.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Data
public class PaymentReadModel {

    @Id
    private String paymentId;

    private String orderId;
    private String customerId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;
    private String currency;

    private String paymentMethod;

    private Instant validatedAt;
    private Instant failedAt;
    private Instant refundedAt;

    private String failureReason;

    public enum PaymentStatus {
        PENDING, VALIDATED, FAILED, REFUNDED
    }
}