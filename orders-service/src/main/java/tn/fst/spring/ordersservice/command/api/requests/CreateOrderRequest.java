package tn.fst.spring.ordersservice.command.api.requests;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerId;
    private List<OrderLineItemRequest> items;
    private AddressRequest shippingAddress;
    private java.math.BigDecimal totalAmount= java.math.BigDecimal.valueOf(5000);
}
