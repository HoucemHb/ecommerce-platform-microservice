package tn.fst.spring.ordersservice.command.api.requests;

import lombok.Data;

@Data
public class OrderLineItemRequest {
    private String productId;
    private int quantity;
    private java.math.BigDecimal unitPrice;
}
