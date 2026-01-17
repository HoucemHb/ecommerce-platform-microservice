package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.time.Instant;
import java.util.List;

@Value
public class PaymentFailedEvent {
    String paymentId;
    String orderId;
    Money amount;
    String reason;
    Instant failedAt;
}
