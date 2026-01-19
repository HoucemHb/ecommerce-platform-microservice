package tn.fst.spring.ordersservice.query.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_lines")
@Data
public class OrderLineReadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private OrderReadModel order;

    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private String currency;
}
