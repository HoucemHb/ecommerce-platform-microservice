package tn.fst.spring.sharedkernel.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class UpdateStockCommand {
    @TargetAggregateIdentifier
    String productId;
    int quantity;
}
