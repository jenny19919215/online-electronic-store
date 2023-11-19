package com.jennyz.electronicstore.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jennyz.electronicstore.utils.Category;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String productName;

    private String description;
    @NotNull
    private Double originalPrice;
    @NotNull
    private Double sellingPrice;

    //private boolean hasDiscountPolicy;
    @Min(0)
    @Max(99)
    private int discountPercentage;
    @NotNull
    private int stockNum;
    @NotNull
    private Category category;
    @NotNull
    private Long createUser;
    @Version
    private int version;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private final LocalDate createTime = LocalDate.now();

    public Product(String productName, Double originalPrice, Double sellingPrice, int stockNum, Category category, Long createUser) {
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.stockNum = stockNum;
        this.category = category;
        this.createUser = createUser;
    }

    public Product(){}
}
