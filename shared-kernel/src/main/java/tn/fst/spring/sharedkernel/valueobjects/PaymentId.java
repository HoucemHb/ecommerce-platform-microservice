package tn.fst.spring.sharedkernel.valueobjects;

import lombok.Value;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PaymentId implements Serializable {
    String id;

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID().toString());
    }

    public static PaymentId of(String id) {
        return new PaymentId(id);
    }
}
