package tn.fst.spring.sharedkernel.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
@ToString
public class ConfirmOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
}