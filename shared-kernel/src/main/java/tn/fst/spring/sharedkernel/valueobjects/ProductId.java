package tn.fst.spring.sharedkernel.valueobjects;

import lombok.Value;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class ProductId implements Serializable {
    String id;

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }

    public static ProductId of(String id) {
        return new ProductId(id);
    }
}
