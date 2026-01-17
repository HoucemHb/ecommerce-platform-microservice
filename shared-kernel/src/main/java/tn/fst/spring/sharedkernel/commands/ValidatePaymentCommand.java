package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.List;

@Value
public class ValidatePaymentCommand {
    @TargetAggregateIdentifier
    String paymentId;
    String orderId;
    String customerId;
    Money amount;
    String paymentMethod;
}
