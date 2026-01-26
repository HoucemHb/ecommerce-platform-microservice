package tn.fst.spring.sharedkernel.valueobjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@ToString
@EqualsAndHashCode
public class Money implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
    private String currency;

    // ✅ Constructeur par défaut pour JPA
    public Money() {}

    // ✅ Constructeur avec annotations Jackson pour Axon
    @JsonCreator
    public Money(
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("currency") String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    // ✅ Getters
    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    // ✅ Méthodes métier
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(
                this.amount.multiply(BigDecimal.valueOf(quantity)),
                this.currency
        );
    }
}