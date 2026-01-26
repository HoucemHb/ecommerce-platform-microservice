package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.List;

@Value
public class CancelOrderCommand {
    @TargetAggregateIdentifier
    String orderId;
    String reason;
}
