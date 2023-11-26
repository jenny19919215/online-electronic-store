package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.entity.Product;
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
class ProductServiceITTest {

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

    @Test
    void test_delete_product_ok() {
        List<Product> productList = productService.findAllProducts();
        assertThat(productList).isNotEmpty();

        Product product = productList.get(0);
        Long id = product.getId();

        productService.deleteProduct(id);
        assertThat(productService.findProduct(id)).isEmpty();

    }

    @Test
    void test_delete_product_not_exist_ok() {
        Long id_not_exist = 123665L;

        assertThat(productService.findProduct(id_not_exist)).isEmpty();
        assertThatThrownBy(() -> productService.deleteProduct(id_not_exist)).isInstanceOf(ProductNotFoundException.class);

    }

    @Test
    void update_product_num_ok() {
        List<Product> productList = productService.findAllProducts();
        assertThat(productList).isNotEmpty();

        Product product = productList.get(0);
        Long id = product.getId();
        int stock = product.getStockNum();

        productService.updateProductStockNum(product, stock + 20);

        assertThat(productService.findProduct(id).get().getStockNum()).isEqualTo(stock + 20);

    }


}
