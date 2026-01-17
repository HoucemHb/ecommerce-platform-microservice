package tn.fst.spring.ordersservice.command.api.requests;

@Data
class AddressRequest {
    private String street;
    private String city;
    private String zipCode;
    private String country;
}
