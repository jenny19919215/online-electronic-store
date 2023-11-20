package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.exception.NotEnoughStockException;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BasketService.class})
@TestPropertySource(properties = {
        "electronicstore.basket.retry.time=3",
})
class BasketServiceTest {

    @SpyBean
    private BasketService basketService;

    @MockBean
    private ProductService productService;

    @MockBean
    private BasketItemRepository basketItemRepository;

    private Product product_not_found;
    private Product product_exist_with_discount;
    private Product product_exist_no_discount;

    private BasketItem basketItem;

    @BeforeEach
    void setUp() {
        product_not_found = new Product("phone", 100.0, 100.0, 20, Category.MOBILES, 123L);
        product_not_found.setId(1L);

        product_exist_with_discount = new Product("pad", 100.0, 100.0, 20, Category.MOBILES, 123L);
        product_exist_with_discount.setId(2L);

        product_exist_no_discount = new Product("laptop", 500.0, 500.0, 20, Category.MOBILES, 123L);
        product_exist_no_discount.setId(3L);

        basketItem = new BasketItem(product_exist_with_discount.getId(), 1L, 5, product_exist_with_discount.getOriginalPrice(), product_exist_with_discount.getDiscountPercentage());

    }

    @Test
    void find_Basket_Items_By_Customer_id_ok() {
        Long id = 111L;
        basketService.findBasketItemsByCustomer(id);
        verify(basketItemRepository, times(1)).findAllByCustomerId(id);
    }

    @Test
    void add_product_not_exist_to_basket_failed() {
        when(productService.findProduct(product_not_found.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> basketService.addProductInBasket(product_not_found.getId(), 1L, 5)).isInstanceOf(ProductNotFoundException.class);

    }


    @Test
    void add_product_with_number_more_than_stock_to_basket_failed() {
        int num = product_exist_with_discount.getStockNum() + 1;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        assertThatThrownBy(() -> basketService.addProductInBasket(product_exist_with_discount.getId(), 1L, num)).isInstanceOf(NotEnoughStockException.class);

    }

    @Test
    void add_product_number_to_basket_data_conflict_failed() {
        int num = 1;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.empty());
        doThrow(OptimisticLockException.class).when(productService).updateProductStockNum(product_exist_with_discount, product_exist_with_discount.getStockNum() - num);
        assertThatThrownBy(() -> basketService.addProductInBasket(product_exist_with_discount.getId(), 1L, num)).isInstanceOf(ResponseStatusException.class);

    }


    @Test
    void add_product_number_to_basket_ok() {
        int numToAdd = 2;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.empty());
        //   doNothing().when(productService).updateProductStockNum(product_exist, product_exist.getStockNum() - numToAdd);

        basketService.addProductInBasket(product_exist_with_discount.getId(), 1L, numToAdd);

        verify(productService, times(1)).updateProductStockNum(product_exist_with_discount, product_exist_with_discount.getStockNum() - numToAdd);
        verify(basketItemRepository, times(1)).save(any());
    }

    @Test
    void remove_product_not_exist_to_basket_failed() {
        when(productService.findProduct(product_not_found.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> basketService.removeProductFromBasket(product_not_found.getId(), 1L, 5)).isInstanceOf(ProductNotFoundException.class);

    }

    @Test
    void remove_product_with_illegal_number_to_basket_failed() {
        int num = basketItem.getProductCount() + 1;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.of(basketItem));
        assertThatThrownBy(() -> basketService.removeProductFromBasket(product_exist_with_discount.getId(), 1L, num)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("not enough basket items to be removed");

    }

    @Test
    void remove_product_number_to_basket_data_conflict_failed() {
        int num = 1;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.of(basketItem));
        doThrow(OptimisticLockException.class).when(productService).updateProductStockNum(product_exist_with_discount, product_exist_with_discount.getStockNum() + num);
        assertThatThrownBy(() -> basketService.removeProductFromBasket(product_exist_with_discount.getId(), 1L, num)).isInstanceOf(ResponseStatusException.class);

    }

    @Test
    void remove_item_number_equals_to_item_count_in_basket_ok() {
        int num = basketItem.getProductCount();
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.of(basketItem));
        basketService.removeProductFromBasket(product_exist_with_discount.getId(), 1L, num);

        verify(productService, times(1)).updateProductStockNum(product_exist_with_discount, product_exist_with_discount.getStockNum() + num);
        verify(basketItemRepository, times(1)).delete(any());

    }

    @Test
    void remove_item_number_smaller_than_item_count_in_basket_ok() {
        int num = basketItem.getProductCount() - 1;
        when(productService.findProduct(product_exist_with_discount.getId())).thenReturn(Optional.of(product_exist_with_discount));
        when(basketItemRepository.findById(any())).thenReturn(Optional.of(basketItem));
        basketService.removeProductFromBasket(product_exist_with_discount.getId(), 1L, num);

        verify(productService, times(1)).updateProductStockNum(product_exist_with_discount, product_exist_with_discount.getStockNum() + num);
        verify(basketItemRepository, times(1)).save(any());

    }

    @Test
    void get_basket_is_empty() {
        when(basketItemRepository.findAllByCustomerId(any())).thenReturn(new ArrayList<>());
        Long customerId = 1L;
        BasketInfo basketInfo = basketService.getBasketInfo(customerId);

        assertThat(basketInfo.getBasketItemList()).isEmpty();
        assertThat(basketInfo.getTotalPrice()).isEqualTo(0.0);
        assertThat(basketInfo.getCustomerId()).isEqualTo(customerId);

    }

    @Test
    void get_basket_with_items() {
        product_exist_with_discount.setDiscountPercentage(20);
        Long customerId = 1L;
        BasketItem basketItem_1 = new BasketItem(product_exist_with_discount.getId(), customerId, 3, product_exist_with_discount.getOriginalPrice(), product_exist_with_discount.getDiscountPercentage());
        BasketItem basketItem_2 = new BasketItem(product_exist_no_discount.getId(), customerId, 4, product_exist_no_discount.getOriginalPrice(), product_exist_no_discount.getDiscountPercentage());

        when(basketItemRepository.findAllByCustomerId(basketItem_1.getCustomerId())).thenReturn(Arrays.asList(basketItem_1,basketItem_2));

        BasketInfo basketInfo = basketService.getBasketInfo(customerId);

        assertThat(basketInfo).isNotNull();
        assertThat(basketInfo.getCustomerId()).isEqualTo(customerId);
        assertThat(basketInfo.getBasketItemList()).isEqualTo(Arrays.asList(basketItem_1,basketItem_2));
        assertThat(basketInfo.getTotalPrice()).isEqualTo(2280.0);


    }
}