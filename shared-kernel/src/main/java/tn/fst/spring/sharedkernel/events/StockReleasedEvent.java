package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class StockReleasedEvent {
    String productId;
    String orderId;
    int quantity;
    Instant releasedAt;
}
