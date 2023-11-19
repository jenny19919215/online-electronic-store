package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    ProductRepository repository;

    @BeforeEach
    void setup(){
        Product product = new Product("phone",100.0,100.0,20, Category.MOBILES, 123L) ;
        entityManager.persist(product);
    }

    @Test
    void should_find_product_by_name() {
        String name = "phone";
        Product product = repository.findByName(name);
        assertThat(product).isNotNull();
        assertThat(product.getProductName()).isEqualTo(name);
        assertThat(product.getCreateUser()).isEqualTo(123L);
    }

    @Test
    void should_not_find_product() {
        String name = "pad";
        Product product = repository.findByName(name);
        assertThat(product).isNull();
    }
}