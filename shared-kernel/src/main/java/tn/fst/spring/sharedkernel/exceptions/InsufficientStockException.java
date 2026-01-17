package tn.fst.spring.sharedkernel.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productId, int requested, int available) {
        super(String.format("Insufficient stock for product %s. Requested: %d, Available: %d",
                productId, requested, available));
    }
}