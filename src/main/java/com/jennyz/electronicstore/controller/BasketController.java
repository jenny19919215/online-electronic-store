package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity getAllBasketItems(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(basketService.getItemsByCustomer(customerId));

    }

    @GetMapping("/{customerId}/totalprice")
    public ResponseEntity<Double> getAllBasketItemsPrice(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(basketService.calculateBasketPrice(customerId));

    }

    @PostMapping("/{customerId}/add/{productId}")
    public ResponseEntity createOrAddProductInBasket(@PathVariable Long productId, @PathVariable Long customerId, @RequestBody int number) throws InterruptedException {
        if (number <= 0) {
            throw new IllegalArgumentException("product count" + number + " to be added should >= 0");
        }
        basketService.addProductInBasket(productId, customerId, number);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @DeleteMapping("/{customerId}/delete/{productId}")
    public ResponseEntity deleteProductInBasket(@PathVariable Long productId, @PathVariable Long customerId, @RequestBody int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("product count should be positive");
        }
        basketService.removeProductFromBasket(productId, customerId, number);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
