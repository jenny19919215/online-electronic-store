package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setup(){
        product = new Product("apple",100.0,100.0,20, Category.MOBILES, 123L) ;
        entityManager.persist(product);
    }


    @Test
    void test_find_by_id_ok() {
        Optional<Product> p = productRepository.findById(product.getId());
        assertThat(p).isNotEmpty();
        assertThat(p.get()).isEqualTo(product);

    }


    @Test
    void test_save_product_ok() {

        Product p1 = new Product("Product A",50.0,50.0,10, Category.OTHERS,1L);

        //testEM.persistAndFlush(b1); the same
        productRepository.save(p1);

        Long savedProductID = p1.getId();

        Product p = productRepository.findById(savedProductID).orElseThrow();

        assertEquals(savedProductID, p.getId());
        assertEquals("Product A", p.getProductName());
        assertEquals(50.0, p.getOriginalPrice());
        assertEquals(10, p.getStockNum());
        assertEquals(Category.OTHERS, p.getCategory());
    }

    @Test
    void test_find_all_product_ok() {
        //FIXME
        List<Product> list = productRepository.findAll();
        assertThat(list).hasSize(2);
     //   assertThat(list.get(0).getProductName()).isEqualTo(product.getProductName());
    }



    @Test
    void should_find_product_by_name() {
        String name = "apple";
        Product product = productRepository.findByName(name);
        assertThat(product).isNotNull();
        assertThat(product.getProductName()).isEqualTo(name);
        assertThat(product.getCreateUser()).isEqualTo(123L);
    }

    @Test
    void should_not_find_product() {
        String name = "pad";
        Product product = productRepository.findByName(name);
        assertThat(product).isNull();
    }

    @Test
    void test_delete_product_not_exist_throw_exception() {
        Long id_not_exist = 123456L;
        assertThat(productRepository.findById(id_not_exist)).isEmpty();
        assertThatThrownBy(()-> productRepository.deleteById(id_not_exist)).isInstanceOf(EmptyResultDataAccessException.class);

    }

    @Test
    void test_delete_product_ok() {
        Long id_exist = product.getId();
        assertThat(productRepository.findById(id_exist)).isPresent();
        productRepository.deleteById(id_exist);
        assertThat(productRepository.findById(id_exist)).isEmpty();

    }
}