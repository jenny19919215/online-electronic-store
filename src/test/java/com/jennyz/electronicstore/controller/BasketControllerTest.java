package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.configuration.WebSecirutyConfig;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.entity.BasketItem;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.service.BasketService;
import com.jennyz.electronicstore.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasketController.class)
@Import(WebSecirutyConfig.class)
class BasketControllerTest {

    @MockBean
    private BasketService basketService;

    @MockBean
    private BasketItemRepository basketItemRepository;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void get_all_basket_items_ok() throws Exception {
        Long id = 1L;
        BasketItem basketItem = new BasketItem(1L, id, 5,
                100.0, 0);



        when(basketService.findBasketItemsByCustomer(id)).thenReturn(List.of(basketItem));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/basket/"+id+"/items").contentType("application" +
                        "/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(id.toString()));

    }

    @Test
    void add_illegal_basket_item_number_in_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        when(basketService.addItemsToBasket(anyLong(),anyLong(),anyInt())).thenReturn(new BasketItem());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/add-to-basket/"+ id).contentType("application" +
                        "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void add_illegal_basket_item_number_in_basket_ok() throws Exception {
        Integer number = 1;
        Long id = 1L;
        when(basketService.addItemsToBasket(anyLong(),anyLong(),anyInt())).thenReturn(new BasketItem(id,id,number,
               40.0, 0));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/add-to-basket/"+ id).contentType("application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("1"))
                .andExpect(jsonPath("$.customerId").value("1"))
                .andExpect(jsonPath("$.productCount").value("1"))
                .andExpect(jsonPath("$.originalPrice").value("40.0"))
                .andExpect(jsonPath("$.discountPercentage").value("0"));
    }


    @Test
    void remove_illegal_basket_item_number_from_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        when(basketService.removeItemsFromBasket(anyLong(),anyLong(),anyInt())).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/remove-from-basket/"+ id).contentType(
                "application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void remove_basket_item_number_from_basket_ok() throws Exception {
        Integer number = 1;
        Long id = 1L;
        when(basketService.removeItemsFromBasket(anyLong(),anyLong(),anyInt())).thenReturn(null);

        MockHttpServletResponse response = this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/remove" +
                                "-from-basket/"+ id).contentType(
                "application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertEquals("",response.getContentAsString());
    }


    @Test
    void calculate_basket_info() throws Exception {
        Long customerId = 1L;
        BasketInfo basketInfo = new BasketInfo(new ArrayList<>(), customerId, 0.0);
        when(basketService.getBasketInfo(customerId)).thenReturn(basketInfo);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/basket/" + customerId + "/calculateBasket")
                        .contentType("application" +
                                "/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basketItemList", Matchers.empty()))
                .andExpect(jsonPath("$.customerId").value("1"))
                .andExpect(jsonPath("$.totalPrice").value("0.0"));
    }

}