package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.exception.ProductAlreadyExist;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductService.class})
class ProductServiceTest {

    public static final Product PRODUCT = new Product("iphone16", 100.0, 100.0, 20, Category.MOBILES, 123L);
    @SpyBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;


    @BeforeEach
    void setUp() {
        PRODUCT.setId(1234L);
    }

    @Test
    void product_insert_product_name_duplicated_failed() {
        String name = "iphone16";
        Product newProduct = new Product("iphone16", 50.0, 50.0, 10, Category.MOBILES, 124L);
        when(productRepository.findByName(name)).thenReturn(PRODUCT);
        assertThatThrownBy(() -> productService.insertProduct(newProduct)).isInstanceOf(ProductAlreadyExist.class);
    }

    @Test
    void product_insert_product_name_OK() {
        String name = "iphone17";
        Product newProduct = new Product(name, 50.0, 50.0, 10, Category.MOBILES, 124L);
        when(productRepository.findByName(name)).thenReturn(null);
        when(productRepository.save(newProduct)).thenReturn(newProduct);

        Product product = productService.insertProduct(newProduct);
        assertThat(product).isNotNull();
        assertThat(product.getProductName()).isEqualTo(name);
    }


    @Test
    void update_date_product_discount_not_exist_failed() {
        Long id = 12345L;
        int percentage = 20;
        when(productRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.updateProductDiscountInfo(id, percentage)).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void update_date_product_discount_ok() {
        Long id = 1234L;
        int percentage = 20;
        when(productRepository.findById(id)).thenReturn(Optional.of(PRODUCT));
        productService.updateProductDiscountInfo(id, percentage);
        verify(productRepository, times(1)).save(PRODUCT);
        assertThat(PRODUCT.getDiscountPercentage()).isEqualTo(percentage);

    }


    @Test
    void update_date_product_stock_num_negative_failed() {
        assertThatThrownBy(() -> productService.updateProductStockNum(PRODUCT, -3)).isInstanceOf(IllegalArgumentException.class).hasMessage("product stock num should be positive");
    }


    @Test
    void update_date_product_null_stock_num_failed() {
        assertThatThrownBy(() -> productService.updateProductStockNum(null, 3)).isInstanceOf(ProductNotFoundException.class).hasMessage("product to save is null");
    }

    @Test
    void update_date_product_null_stock_num_ok() {
        int num = 10;
        productService.updateProductStockNum(PRODUCT, num);
        verify(productRepository, times(1)).save(PRODUCT);
        assertThat(PRODUCT.getStockNum()).isEqualTo(num);
    }


    @Test
    void delete_product_not_exist_failed() {
        Long idNotFound = 11111L;
        when(productRepository.findById(idNotFound)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.deleteProduct(idNotFound)).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void delete_product_ok() {
        Long id = 1234L;
        when(productRepository.findById(id)).thenReturn(Optional.of(PRODUCT));
        productService.deleteProduct(id);
        verify(productRepository, times(1)).deleteById(id);
    }


}