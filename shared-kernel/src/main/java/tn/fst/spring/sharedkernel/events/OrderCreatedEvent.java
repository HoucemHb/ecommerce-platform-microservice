package tn.fst.spring.sharedkernel.events;

import lombok.Value;
import tn.fst.spring.sharedkernel.valueobjects.*;

import java.time.Instant;
import java.util.List;

@Value
public class OrderCreatedEvent {
    String orderId;
    String customerId;
    List<OrderLineItem> items;
    Address shippingAddress;
    Money totalAmount;
    Instant createdAt;
}
