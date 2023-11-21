package com.jennyz.electronicstore.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
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
    @NotNull
    private Double originalPrice;
    @NotNull
    private int discountPercentage;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createTime ;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate updateTime;



    public BasketItem(){
    }
    @Builder
    public BasketItem(Long productId, Long customerId, Integer productCount, Double originalPrice, int discountPercentage) {
        this.productId = productId;
        this.customerId = customerId;
        this.productCount = productCount;
        this.originalPrice = originalPrice;
        this.discountPercentage = discountPercentage;
    }
}
