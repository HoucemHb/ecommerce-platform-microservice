package tn.fst.spring.sharedkernel.events;

import lombok.Value;

import java.time.Instant;

@Value
public class StockUpdatedEvent {
    String productId;
    int oldStock;
    int newStock;
}
