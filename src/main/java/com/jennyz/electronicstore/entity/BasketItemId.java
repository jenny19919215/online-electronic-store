package com.jennyz.electronicstore.entity;

import lombok.Data;

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
