package tn.fst.spring.sharedkernel.events;
import lombok.Value;
import java.time.Instant;
import java.util.List;

@Value
public class StockReservationFailedEvent {
    String productId;
    String orderId;
    int requestedQuantity;
    int availableStock;
    String reason;
}
