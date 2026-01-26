package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class StockReservedEvent {
    String productId;
    String orderId;
    int quantity;
    int remainingStock;
    Instant reservedAt;
}
