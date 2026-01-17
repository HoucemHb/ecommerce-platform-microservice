package tn.fst.spring.sharedkernel.valueobjects;

import lombok.Value;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class Money implements Serializable {
    BigDecimal amount;
    String currency;

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}
