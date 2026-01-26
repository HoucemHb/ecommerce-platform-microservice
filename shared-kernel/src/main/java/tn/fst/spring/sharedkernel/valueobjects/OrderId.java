package tn.fst.spring.sharedkernel.valueobjects;

import lombok.Value;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class OrderId implements Serializable {
    String id;

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public static OrderId of(String id) {
        return new OrderId(id);
    }
}
