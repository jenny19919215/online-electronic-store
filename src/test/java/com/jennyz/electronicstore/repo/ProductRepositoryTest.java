package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.utils.Category;
import com.jennyz.electronicstore.Entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    private Product product;


    @BeforeEach
    public void setupTestData(){
        // Given : Setup object or precondition
       /* product = Product.builder()
                .productName("ds").originalPrice(100.0).stockNum(20).category(Category.MOBILES).sellingPrice(100.0)
                .createUser(1L).build();*/

      product = new Product("phone",100.0,100.0,20, Category.MOBILES, 1L) ;

    }

    @Test
    @DisplayName("JUnit test for save employee operation")
    public void givenEmployeeObject_whenSave_thenReturnSaveEmployee(){


        // When : Action of behavious that we are going to test
        Product saveProduct = productRepository.save(product);

        // Then : Verify the output


        assertThat(saveProduct).isNotNull();
        assertThat(saveProduct.getId()).isPositive();

    }


}