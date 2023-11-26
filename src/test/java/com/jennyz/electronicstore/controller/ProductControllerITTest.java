package com.jennyz.electronicstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jennyz.electronicstore.dto.ProductDTO;
import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.service.ProductService;
import com.jennyz.electronicstore.utils.Category;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerITTest {

    private static final ObjectMapper om = new ObjectMapper();
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        om.registerModule(new JavaTimeModule());
    }


    @Test
    void should_return_product_list_ok() throws Exception {
        MockHttpServletResponse response =
                this.mockMvc.perform(MockMvcRequestBuilders.get(ProductController.API_BASE_PATH).contentType(
                                "application" +
                                        "/json"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.*").isArray()).andReturn().getResponse();

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

        String serializedList = om.writeValueAsString(productDTO_valid);

        Product product_name_valid = new Product(productDTO_valid.productName(), productDTO_valid.originalPrice(),
                productDTO_valid.sellingPrice(), productDTO_valid.stockNum(), Category.FRUITS, 123L);
        // when(productService.insertProduct(product_name_valid)).thenReturn(product_name_valid);

        Product actualProduct = om.readValue(this.mockMvc.perform(MockMvcRequestBuilders
                        .post(ProductController.API_BASE_PATH + "/create")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json")
                        .content(serializedList))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(), Product.class);

        Assert.assertTrue(new ReflectionEquals(product_name_valid, "id").matches(actualProduct));
    }

    @Test
    void delete_Product_ok() throws Exception {
        Long id = 1L;
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(ProductController.API_BASE_PATH + "/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("1"));

    }

    @Test
    void update_product_not_exist_promotion_failed() throws Exception {
        Long id = 1234L;
        Integer percentage = 30;
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

        Integer percentage = 30;
        Product p3 = new Product("samsung", 400.0, 400.0, 30, Category.MOBILES, 1L);
        productService.insertProduct(p3);
        Long id = p3.getId();
        this.mockMvc.perform(MockMvcRequestBuilders
                        .put(ProductController.API_BASE_PATH + "/" + id + "/update-discount")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "123456"))
                        .contentType("application/json")
                        .content(percentage.toString()))
                .andDo(print())
                .andExpect(status().isOk());

        assertEquals(percentage, productService.findProduct(id).get().getDiscountPercentage());
    }
}
