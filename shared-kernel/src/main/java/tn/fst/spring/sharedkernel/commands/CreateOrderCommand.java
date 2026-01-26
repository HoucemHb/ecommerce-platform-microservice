package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.fst.spring.sharedkernel.valueobjects.*;

import java.util.List;

@Value
public class CreateOrderCommand {
    @TargetAggregateIdentifier
    String orderId;
    String customerId;
    List<OrderLineItem> items;
    Address shippingAddress;
    Money totalAmount;
}


