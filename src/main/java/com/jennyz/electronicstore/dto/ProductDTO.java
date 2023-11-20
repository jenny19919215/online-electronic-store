package com.jennyz.electronicstore.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public record ProductDTO(@NotBlank String productName, String description, @NotNull Double originalPrice,
                         @NotNull Double sellingPrice,
                         boolean hasDiscountPolicy, @NotNull @Min(1) Integer stockNum, @NotBlank String category,
                         @NotNull Long createUser) {


}
