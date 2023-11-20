package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.BasketItemId;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BasketItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    BasketItemRepository basketItemRepository;

    @BeforeEach
    void setup(){
        BasketItem basketItem = new BasketItem(1L,1L,5,20.0,20);
        BasketItem basketItem1 = new BasketItem(2L,1L,6,30.0,0);
        entityManager.persist(basketItem);
        entityManager.persist(basketItem1);

    }

    @Test
    void test_save_basket_item_ok() {

        BasketItem b1 = new BasketItem(3L,1L,5,20.0,20);

        //testEM.persistAndFlush(b1); the same
        basketItemRepository.save(b1);

        BasketItemId saveBasketItemId = new BasketItemId(b1.getProductId(),b1.getCustomerId());

        BasketItem basketItem = basketItemRepository.findById(saveBasketItemId).orElseThrow();

        assertEquals(saveBasketItemId, new BasketItemId(basketItem.getProductId(),basketItem.getCustomerId()));

        assertEquals(5, basketItem.getProductCount());
        assertEquals(20.0, basketItem.getOriginalPrice());
        assertEquals(20, basketItem.getDiscountPercentage());
    }


    @Test
    void should_find_all_basket_items_by_customer_id() {
        List<BasketItem> items = basketItemRepository.findAllByCustomerId(1L);
        assertThat(items).hasSize(2);
    }

    @Test
    void should_find_0_basket_items() {
        List<BasketItem> items = basketItemRepository.findAllByCustomerId(134L);
        assertThat(items).isEmpty();

    }

    @Test
    void test_delete_basket_ok() {
        BasketItemId id = new BasketItemId(1L,1L);
        BasketItem item= basketItemRepository.findById(id).get();

        assertThat(item).isNotNull();
        basketItemRepository.delete(item);

        assertThat(basketItemRepository.findById(id)).isEmpty();

    }

}