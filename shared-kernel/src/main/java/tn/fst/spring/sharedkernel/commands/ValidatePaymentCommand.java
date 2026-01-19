package tn.fst.spring.sharedkernel.commands;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import tn.fst.spring.sharedkernel.valueobjects.Money;

@Value
public class ValidatePaymentCommand {
    @TargetAggregateIdentifier
    String paymentId;
    String orderId;
    String customerId;
    Money totalAmount;  // Changed from 'amount' to 'totalAmount'
    String paymentMethod;
}