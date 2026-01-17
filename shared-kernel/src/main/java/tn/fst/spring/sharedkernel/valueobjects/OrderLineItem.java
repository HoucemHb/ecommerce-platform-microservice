package tn.fst.spring.sharedkernel.valueobjects;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class OrderLineItem {
    String productId;
    int quantity;
    Money unitPrice;
}
