package tn.fst.spring.productservice.query.models;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "products")
@Data
public class ProductReadModel {

    @Id
    private String productId;

    private String name;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;
    private String currency;

    private int availableStock;

    @ElementCollection
    @CollectionTable(name = "product_reservations",
            joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "order_id")
    @Column(name = "quantity")
    private Map<String, Integer> reservations = new HashMap<>();
}