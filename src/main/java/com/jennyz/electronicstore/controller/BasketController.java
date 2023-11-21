package com.jennyz.electronicstore.controller;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.BasketItemId;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.service.BasketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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
    public ResponseEntity<BasketInfo> calculateBasket(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.OK).body(basketService.getBasketInfo(customerId));
    }

    @PostMapping("/{customerId}/add-to-basket/{productId}")
    public ResponseEntity<BasketItem> createOrUpdateItemInBasket(@PathVariable Long productId,
                                                                 @PathVariable Long customerId,
                                                                 @RequestBody int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("product count " + number + " to be added should >= 0");
        }

        return ResponseEntity.status(HttpStatus.OK).body(basketService.addItemsToBasket(productId, customerId, number));
    }

    @PostMapping("/{customerId}/remove-from-basket/{productId}")
    public ResponseEntity<BasketItem> removeItemsFromBasket(@PathVariable Long productId, @PathVariable Long customerId,
                                                          @RequestBody int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("product count" + number + " to be deleted should >= 0");
        }
        return ResponseEntity.status(HttpStatus.OK).body(basketService.removeItemsFromBasket(productId, customerId,
                number));
    }


    @DeleteMapping("/{customerId}/delete/{productId}")
    public ResponseEntity deleteItemsInBasket1(@PathVariable @NotNull Long customerId,
                                               @PathVariable @NotNull Long productId) {
        basketService.deleteBasketItemById(new BasketItemId(productId, customerId));
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
