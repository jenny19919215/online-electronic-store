package com.jennyz.electronicstore.dto;

import com.jennyz.electronicstore.Entity.BasketItem;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BasketInfo {

    private UUID id;

    private List<BasketItem> basketItemList;

    private Long customerId;

    private Double totalPrice;

    public BasketInfo(List<BasketItem> basketItemList,Long customerId, Double totalPrice) {
        this.id = UUID.randomUUID();
        this.basketItemList = basketItemList;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
    }
}
