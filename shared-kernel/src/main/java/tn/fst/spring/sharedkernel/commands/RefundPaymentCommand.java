package tn.fst.spring.sharedkernel.commands;
import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.fst.spring.sharedkernel.valueobjects.Money;

import java.util.List;

@Value
public class RefundPaymentCommand {
    @TargetAggregateIdentifier
    String paymentId;
    String orderId;
    Money amount;
}
