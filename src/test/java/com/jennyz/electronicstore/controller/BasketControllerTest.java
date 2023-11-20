package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.service.BasketService;
import com.jennyz.electronicstore.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BasketController.class)
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
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(id.toString()));

    }

    @Test
    void add_illegal_basket_item_number_in_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        doNothing().when(basketService).addProductInBasket(anyLong(),anyLong(),anyInt());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/add/"+ id).contentType("application" +
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
        doNothing().when(basketService).addProductInBasket(anyLong(),anyLong(),anyInt());

        this.mockMvc.perform(MockMvcRequestBuilders.post("/basket/"+id+"/add/"+ id).contentType("application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void delete_illegal_basket_item_number_from_basket_throw_exception() throws Exception {
        Integer number = -1;
        Long id = 1L;
        doNothing().when(basketService).removeProductFromBasket(anyLong(),anyLong(),anyInt());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/basket/"+id+"/delete/"+ id).contentType("application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));
    }

    @Test
    void delete_illegal_basket_item_number_from_basket_ok() throws Exception {
        Integer number = 1;
        Long id = 1L;
        doNothing().when(basketService).removeProductFromBasket(anyLong(),anyLong(),anyInt());

        this.mockMvc.perform(MockMvcRequestBuilders.delete("/basket/"+id+"/delete/"+ id).contentType("application" +
                                "/json")
                        .content(number.toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}