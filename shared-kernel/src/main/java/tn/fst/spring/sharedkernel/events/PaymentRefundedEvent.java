package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.time.Instant;

@Value
public class PaymentRefundedEvent {
    String paymentId;
    String orderId;
    Money amount;
    Instant refundedAt;
}
