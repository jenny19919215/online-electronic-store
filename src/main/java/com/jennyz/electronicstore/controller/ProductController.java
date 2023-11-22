package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.dto.ProductDTO;
import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.service.ProductService;
import com.jennyz.electronicstore.utils.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = ProductController.API_BASE_PATH)
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    public static final String API_BASE_PATH = "/product";

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProductList() {
        LOGGER.info("find all products");
        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.findAllProducts());

    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        LOGGER.info("create new product {}", productDTO);
        Product newProduct = new Product(productDTO.productName(), productDTO.originalPrice(),
                productDTO.sellingPrice(),
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

    @Operation(summary = "Update product discount information by id", description = "Returns http status")
    @PutMapping("/{productId}/update-discount")
    public ResponseEntity updateProductPromoInfo(@PathVariable Long productId, @RequestBody @Parameter(name =
            "discountPercentage", description = "percentage to be applied from 0 to 100", example = "50")
    Integer percentageToReduce) {
        LOGGER.info("update product {} discount to {} percent", productId, percentageToReduce);

        productService.updateProductDiscountInfo(productId, percentageToReduce);

        return ResponseEntity.status(HttpStatus.OK).build();

    }


}
