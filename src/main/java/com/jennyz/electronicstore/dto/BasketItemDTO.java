package com.jennyz.electronicstore.dto;

import com.jennyz.electronicstore.entity.BasketItem;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record BasketItemDTO(@NotNull Long productId, @NotNull Long customerId, @Min(1) Integer itemCount,
                            @NotNull int discountPercentage, @NotNull Double originalPrice) {


    public BasketItem toBasketItem() {
        return BasketItem.builder()
                .productId(this.productId())
                .customerId(this.customerId())
                .discountPercentage(this.discountPercentage())
                .originalPrice(this.originalPrice())
                .productCount(this.itemCount()).build();

    }
}

