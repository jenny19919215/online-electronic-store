package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.BasketItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BasketItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    BasketItemRepository repository;

    @BeforeEach
    void setup(){
        BasketItem basketItem = new BasketItem(1L,1L,5,20.0,20);
        BasketItem basketItem1 = new BasketItem(2L,1L,6,30.0,0);
        entityManager.persist(basketItem);
        entityManager.persist(basketItem1);

    }

    @Test
    void should_find_all_basket_items_by_customer_id() {
        List<BasketItem> items = repository.findAllByCustomerId(1L);
        assertThat(items).hasSize(2);
    }

    @Test
    void should_find_0_basket_items() {
        List<BasketItem> items = repository.findAllByCustomerId(134L);
        assertThat(items).isEmpty();

    }
}