package tn.fst.spring.ordersservice.command.api.requests;

import lombok.Data;

@Data
class AddressRequest {
    private String street;
    private String city;
    private String zipCode;
    private String country;
}
