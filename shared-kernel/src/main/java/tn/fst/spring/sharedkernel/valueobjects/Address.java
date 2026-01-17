package tn.fst.spring.sharedkernel.valueobjects;

import lombok.Value;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Value
public class Address implements Serializable {
    String street;
    String city;
    String zipCode;
    String country;
}
