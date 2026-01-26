package tn.fst.spring.sharedkernel.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Value
public class DeleteProductCommand {

    @TargetAggregateIdentifier
    String productId;

    @JsonCreator
    public DeleteProductCommand(@JsonProperty("productId") String productId) {
        this.productId = productId;
    }
}
