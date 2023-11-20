package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long id;

    @BeforeEach
    void setUp() {
        Product product = new Product("iphone16", 100.0, 100.0, 20, Category.MOBILES, 123L);
        productRepository.save(product);
        id = product.getId();
    }

    @Test
    void test_find_and_insert_product_ok() {
        List<Product> productList = productService.findAllProducts();
        assertThat(productList).isNotEmpty();

        Product product = productList.get(0);
        Long id = product.getId();

        Product newP = productService.findProduct(id).get();
        assertThat(newP).isEqualTo(product);

    }


    @Test
    void test_update_product_not_found() {
        Long newId = id + 2;
        assertThatThrownBy(() -> productService.updateProductDiscountInfo(newId, 20)).isInstanceOf(ProductNotFoundException.class);

    }

    @Test
    void test_update_product_discount_info_ok() {
        productService.updateProductDiscountInfo(id, 46);

        Product p = productService.findProduct(id).get();
        assertThat(p.getDiscountPercentage()).isEqualTo(46);

    }

    //TODO  deleteProduct  updateProductStockNum

}
