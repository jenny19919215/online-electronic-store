package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.utils.Category;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.dto.ProductDTO;
import com.jennyz.electronicstore.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProductList() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAllProducts());

    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product newProduct = new Product(productDTO.productName(), productDTO.originalPrice(), productDTO.sellingPrice(),
                productDTO.stockNum(), Category.valueOf(productDTO.category()), productDTO.createUser());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.insertProduct(newProduct));

    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long productId) {
        LOGGER.info("Delete product {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(productId);
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity updateProductPromoInfo(@PathVariable Long productId,  @RequestBody Integer percentageToReduce) {
        LOGGER.info("update product {} discount to {} percent", productId, percentageToReduce);

        productService.updateProductDiscountInfo(productId, percentageToReduce);

        return ResponseEntity.status(HttpStatus.OK).build();

    }


}
