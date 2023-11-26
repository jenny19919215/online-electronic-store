package com.jennyz.electronicstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.entity.BasketItem;
import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.service.BasketService;
import com.jennyz.electronicstore.service.ProductService;
import com.jennyz.electronicstore.utils.Category;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BasketControllerIT {
    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private BasketService basketService;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    private static Long product_id;


    @BeforeAll
    static void setUp() {
        om.registerModule(new JavaTimeModule());
    }


    @Test
    @Order(5)
    void get_all_basket_items_ok() throws Exception {
        Long id = 1L;

        this.mockMvc.perform(MockMvcRequestBuilders.get("/basket/" + id + "/items").contentType("application" +
                        "/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(id.toString()))
                .andExpect(jsonPath("$[0].productCount").value(6));

    }

    @Test
    @Order(1)
    void add_illegal_basket_item_number_in_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        //   when(basketService.addItemsToBasket(anyLong(),anyLong(),anyInt())).thenReturn(new BasketItem());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/" + id + "/add-to-basket/" + id).contentType(
                                "application" +
                                        "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    @Order(2)
    void add_basket_item_in_basket_ok() throws Exception {
        Integer number = 8;
        Long id = 1L;
      /*  when(basketService.addItemsToBasket(anyLong(),anyLong(),anyInt())).thenReturn(new BasketItem(id,id,number,
               40.0, 0));*/
        Product product = new Product("phone", 100.0, 100.0, 20, Category.MOBILES, 1L);
        product.setDiscountPercentage(20);
        productService.insertProduct(product);

        product_id = product.getId();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/" + id + "/add-to-basket/" + product_id).contentType(
                                "application" +
                                        "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product_id.toString()))
                .andExpect(jsonPath("$.customerId").value("1"))
                .andExpect(jsonPath("$.productCount").value(number.toString()))
                .andExpect(jsonPath("$.originalPrice").value("100.0"))
                .andExpect(jsonPath("$.discountPercentage").value("20"));


    }


    @Test
    @Order(3)
    void remove_illegal_basket_item_number_from_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        //   when(basketService.removeItemsFromBasket(anyLong(),anyLong(),anyInt())).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/" + id + "/remove-from-basket/" + id).contentType(
                                "application" +
                                        "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    @Order(4)
    void remove_basket_item_number_from_basket_ok() throws Exception {
        Integer number = 2;

        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/" + 1L +
                                "/remove" +
                                "-from-basket/" + product_id).contentType(
                                "application" +
                                        "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        BasketItem actualBasketItem = om.readValue(response.getContentAsString(), BasketItem.class);
        assertEquals(6, actualBasketItem.getProductCount());
    }


    @Test
    @Order(6)
    void calculate_basket_info() throws Exception {
        Long customerId = 1L;
        BasketInfo basketInfo = new BasketInfo(new ArrayList<>(), customerId, 0.0);

      /*  Product product2= new Product("product 2",200.0,200.0,50, Category.BOOKS,1L);
        product2.setDiscountPercentage(20);
        productService.insertProduct(product2);


        basketService.addItemsToBasket(2L,1L,2);*/

        this.mockMvc.perform(MockMvcRequestBuilders.get("/basket/" + customerId + "/calculateBasket")
                        .contentType("application" +
                                "/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketItemList", hasSize(1)))
                .andExpect(jsonPath("$.customerId").value("1"))
                .andExpect(jsonPath("$.totalPrice").value("540.0"));
    }

}