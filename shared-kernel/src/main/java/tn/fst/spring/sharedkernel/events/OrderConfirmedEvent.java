package tn.fst.spring.sharedkernel.events;

import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class OrderConfirmedEvent {
    String orderId;
    Instant confirmedAt;
}
