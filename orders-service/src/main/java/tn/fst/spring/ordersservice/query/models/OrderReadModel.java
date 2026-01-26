package tn.fst.spring.ordersservice.query.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class OrderReadModel {

    @Id
    private String orderId;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalAmount;
    private String currency;

    private String shippingStreet;
    private String shippingCity;
    private String shippingZipCode;
    private String shippingCountry;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "order")
    private List<OrderLineReadModel> items = new ArrayList<>();

    private Instant createdAt;
    private Instant confirmedAt;
    private Instant cancelledAt;

    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
