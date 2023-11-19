package com.jennyz.electronicstore.Entity;

import lombok.Data;
import lombok.Value;

import javax.persistence.Entity;
import java.io.Serializable;

@Data
public class BasketItemId implements Serializable {
    private Long productId;

    private Long customerId;

    public BasketItemId(){}
    public BasketItemId(Long productId, Long customerId) {
        this.productId = productId;
        this.customerId = customerId;
    }

}
