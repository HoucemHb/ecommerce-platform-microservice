package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.time.Instant;
import java.util.List;

@Value
public class PaymentValidatedEvent {
    String paymentId;
    String orderId;
    String customerId;
    Money amount;
    String paymentMethod;
    Instant validatedAt;
}
