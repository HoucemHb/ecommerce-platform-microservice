package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.util.List;
import tn.fst.spring.sharedkernel.valueobjects.Money;

@Value
public class CreateProductCommand {
    @TargetAggregateIdentifier
    String productId;
    String name;
    String description;
    Money price;
    int initialStock;
}
