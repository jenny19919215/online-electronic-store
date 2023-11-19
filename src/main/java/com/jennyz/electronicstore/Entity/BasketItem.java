package com.jennyz.electronicstore.Entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@IdClass(BasketItemId.class)
public class BasketItem {
    @Id
    @NotNull
    private Long productId;
    @Id
    @NotNull
    private Long customerId;
    @Min(1)
    private Integer productCount;

    private Double originalPrice;

    private int discountPercentage;

    private LocalDate createTime = LocalDate.now();

    private LocalDate updateTime;



    public BasketItem(){
    }

    public BasketItem(Long productId, Long customerId, Integer productCount, Double originalPrice, int discountPercentage) {
        this.productId = productId;
        this.customerId = customerId;
        this.productCount = productCount;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
    }
}
