package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.List;

@Value
public class ReleaseStockCommand {
    @TargetAggregateIdentifier
    String productId;
    String orderId;
    int quantity;
}
