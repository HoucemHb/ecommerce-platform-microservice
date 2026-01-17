package tn.fst.spring.sharedkernel.events;

import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class NotificationSentEvent {
    String notificationId;
    String orderId;
    String customerId;
    String type;
    String channel;
    Instant sentAt;
}
