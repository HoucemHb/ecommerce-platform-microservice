package tn.fst.spring.sharedkernel.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Value;
@Value
public class DeleteProductEvent {

    String productId;

    @JsonCreator
    public DeleteProductEvent(@JsonProperty("productId") String productId) {
        this.productId = productId;
    }
}
