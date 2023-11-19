package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/{customerId}/items")
    public ResponseEntity<List<BasketItem>> getAllBasketItems(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(basketService.findBasketItemsByCustomer(customerId));

    }

    @GetMapping("/{customerId}/calculateBasket")
    public ResponseEntity<BasketInfo> getAllBasketItemsPrice(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(basketService.getBasketInfo(customerId));

    }

    @PostMapping("/{customerId}/add/{productId}")
    public ResponseEntity createOrAddProductInBasket(@PathVariable Long productId, @PathVariable Long customerId, @RequestBody int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("product count" + number + " to be added should >= 0");
        }
        basketService.addProductInBasket(productId, customerId, number);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @DeleteMapping("/{customerId}/delete/{productId}")
    public ResponseEntity deleteProductInBasket(@PathVariable Long productId, @PathVariable Long customerId, @RequestBody int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("product count" + number + " to be deleted should >= 0");
        }
        basketService.removeProductFromBasket(productId, customerId, number);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
