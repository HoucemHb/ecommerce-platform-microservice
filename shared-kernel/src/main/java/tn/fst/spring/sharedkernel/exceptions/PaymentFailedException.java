package tn.fst.spring.sharedkernel.exceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String reason) {
        super("Payment failed: " + reason);
    }
}