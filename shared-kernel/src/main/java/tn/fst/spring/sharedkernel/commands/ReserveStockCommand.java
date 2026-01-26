package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.List;

@Value
public class ReserveStockCommand {
    @TargetAggregateIdentifier
    String productId;
    String orderId;
    int quantity;
}