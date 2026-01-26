package tn.fst.spring.sharedkernel.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@ToString
public class ConfirmOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;

    @JsonCreator
    public ConfirmOrderCommand(@JsonProperty("orderId") String orderId) {
        this.orderId = orderId;
    }
}
