package tn.fst.spring.sharedkernel.events;

import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class OrderCancelledEvent {
    String orderId;
    String reason;
    Instant cancelledAt;
}
