package com.jennyz.electronicstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jennyz.electronicstore.configuration.WebSecirutyConfig;
import com.jennyz.electronicstore.dto.ProductDTO;
import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.service.ProductService;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(WebSecirutyConfig.class)
class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void should_return_product_list_ok() throws Exception {
        Product product = new Product("apple", 100.0, 100.0,
                20, Category.MOBILES, 123L);
        product.setId(12345678L);
        when(productService.findAllProducts()).thenReturn(List.of(product));

        this.mockMvc.perform(MockMvcRequestBuilders.get(ProductController.API_BASE_PATH).contentType(
                        "application" +
                                "/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(12345678)));
    }

    @Test
    void create_product_name_not_valid() throws Exception {
        ProductDTO productDTO_name_invalid = new ProductDTO(" ", "NA", 100.0,
                100.0, false, 20,
                Category.FRUITS.toString(), 123L);
        ObjectMapper mapper = new ObjectMapper();
        String serializedList = mapper.writeValueAsString(productDTO_name_invalid);



        this.mockMvc.perform(MockMvcRequestBuilders.post(ProductController.API_BASE_PATH + "/create")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                .contentType("application/json")
                .content(serializedList))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void create_product_name_ok() throws Exception {
        ProductDTO productDTO_valid = new ProductDTO("pear", "NA", 100.0, 100.0, false, 20,
                Category.FRUITS.toString(), 123L);

        ObjectMapper mapper = new ObjectMapper();
        String serializedList = mapper.writeValueAsString(productDTO_valid);



        Product product_name_valid = new Product(productDTO_valid.productName(), productDTO_valid.originalPrice(),
                productDTO_valid.sellingPrice(), productDTO_valid.stockNum(), Category.FRUITS, 123L);
        when(productService.insertProduct(product_name_valid)).thenReturn(product_name_valid);


        this.mockMvc.perform(MockMvcRequestBuilders
                        .post(ProductController.API_BASE_PATH + "/create")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                .contentType("application/json")
                        .content(serializedList))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", is("pear")));

    }

    @Test
    void delete_Product_ok() throws Exception {
        Long id = 1234L;
        doNothing().when(productService).deleteProduct(id);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(ProductController.API_BASE_PATH + "/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("1234"));

    }

    @Test
    void update_product_not_exist_promotion_failed() throws Exception {
        Long id = 1234L;
        Integer percentage = 30;
        doThrow(ProductNotFoundException.class).when(productService).updateProductDiscountInfo(id, percentage);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(ProductController.API_BASE_PATH + "/" + id + "/update-discount")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json")
                        .content(percentage.toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProductNotFoundException));
    }

    @Test
    void update_product_promotion_ok() throws Exception {
        Long id = 1234L;
        Integer percentage = 30;
        doNothing().when(productService).updateProductDiscountInfo(id, percentage);

        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(ProductController.API_BASE_PATH + "/" + id + "/update-discount")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json")
                        .content(percentage.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}