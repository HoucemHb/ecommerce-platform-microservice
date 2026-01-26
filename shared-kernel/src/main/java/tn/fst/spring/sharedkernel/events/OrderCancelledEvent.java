package tn.fst.spring.sharedkernel.events;

import lombok.Value;
import tn.fst.spring.sharedkernel.valueobjects.OrderLineItem;

import java.time.Instant;
import java.util.List;

@Value
public class OrderCancelledEvent {
    String orderId;
    String reason;
    Instant cancelledAt;
    List<OrderLineItem> items; // OrderItem contient productId et quantity

}
