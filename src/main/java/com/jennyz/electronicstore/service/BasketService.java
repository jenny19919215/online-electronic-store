package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.BasketItemId;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.exception.BasketItemNotFoundException;
import com.jennyz.electronicstore.exception.NotEnoughStockException;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.OptimisticLockException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasketService.class);
    private final int maxRetries;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final BasketItemRepository basketItemRepository;

    private final ProductService productService;

    public BasketService(BasketItemRepository basketItemRepository, ProductService productService, @Value("${electronicstore.basket.retry.time}") int retry) {
        this.basketItemRepository = basketItemRepository;
        this.productService = productService;
        this.maxRetries = retry;
    }

    public List<BasketItem> findBasketItemsByCustomer(Long customerId) {
        LOGGER.info("find basket item list by customerId {}", customerId);
        return basketItemRepository.findAllByCustomerId(customerId);

    }

    @Transactional
    public void addProductInBasket(Long productId, Long customerId, int numberToAdd) {
        LOGGER.info("start to add {} product {} to basket for {}", numberToAdd, productId, customerId);
        for (int i = 0; i < maxRetries; i++) {
            try {
                Product product = productService.findProduct(productId).orElseThrow(() -> new ProductNotFoundException(String.format("product to add {} not exist", productId)));

                int currentStock = product.getStockNum();
                if (numberToAdd > currentStock) {
                    throw new NotEnoughStockException("Not enough stock exist");
                }
                productService.updateProductStockNum(product, currentStock - numberToAdd);
                createOrUpdateBasketItemCount(product, customerId, numberToAdd);
                break; // Break out of loop if successful
            } catch (OptimisticLockException e) {
                LOGGER.error("Data conflict encountered when add product from basket", e);
                // If it's the last iteration, throw the exception. Otherwise, continue to the next iteration.
                if (i == maxRetries - 1) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Data conflict after multiple retries.");
                }
            }
        }
    }


    @Transactional
    public void removeProductFromBasket(Long productId, Long customerId, int number) {
        LOGGER.info("start to remove {} product {} from basket for {}", number, productId, customerId);
        for (int i = 0; i < maxRetries; i++) {
            try {
                Product product = productService.findProduct(productId).orElseThrow(() -> new ProductNotFoundException(String.format("product to remove {} not exist", productId)));
                BasketItem basketItem = basketItemRepository.findById(new BasketItemId(productId, customerId)).orElseThrow(() -> new BasketItemNotFoundException("basketItem not found"));
                int itemNum = basketItem.getProductCount();
                if (itemNum < number) {
                    throw new IllegalArgumentException("not enough basket items to be removed");
                }

                productService.updateProductStockNum(product, product.getStockNum() + number);
                if (itemNum == number) {
                    basketItemRepository.delete(basketItem);
                } else {
                    basketItem.setProductCount(itemNum - number);
                    basketItemRepository.save(basketItem);
                }
                break; // Break out of loop if successful
            } catch (OptimisticLockException e) {
                LOGGER.error("Data conflict encountered when remove product from basket", e);
                // If it's the last iteration, throw the exception. Otherwise, continue to the next iteration.
                if (i == maxRetries - 1) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Data conflict after multiple retries.");
                }
            }
        }
    }

    public BasketInfo getBasketInfo(Long customerId) {
        List<BasketItem> list = findBasketItemsByCustomer(customerId);
        if (Objects.isNull(list) || list.isEmpty()) {
            return new BasketInfo(new ArrayList<>(), customerId, 0.0);
        }
        double totalPrice = getTotalPrice(list);
        return new BasketInfo(list, customerId, totalPrice);
    }

    public Double calculateBasketPrice(Long customerId) {
        List<BasketItem> list = findBasketItemsByCustomer(customerId);
        double totalPrice = 0.0;
        if (Objects.isNull(list) || list.isEmpty()) {
            return totalPrice;
        }
        totalPrice = getTotalPrice(list);
        return totalPrice;

    }

    private double getTotalPrice(List<BasketItem> list) {
        double totalPrice = 0.0;
        for (BasketItem basketItem : list) {
            int num = basketItem.getProductCount();
            Double originalPrice = basketItem.getOriginalPrice();
            int percentage = basketItem.getDiscountPercentage();

            if (percentage == 0) {
                totalPrice += num * originalPrice;
                continue;
            }

            double ratio = 1.0 - percentage / 100.0;
            if (num % 2 == 0) {
                //second one get percentage discount
                totalPrice += num * originalPrice * (1 + ratio) / 2;
            } else {
                totalPrice += (num - 1) * originalPrice * (1 + ratio) / 2 + originalPrice;
            }

        }
        return totalPrice;
    }

    private void createOrUpdateBasketItemCount(Product product, Long customerId, int numberToAdd) {
        Long productId = product.getId();
        Optional<BasketItem> basketItem = basketItemRepository.findById(new BasketItemId(productId, customerId));
        BasketItem basketItemToSave;
        if (basketItem.isPresent()) {
            basketItemToSave = basketItem.get();
            basketItemToSave.setProductCount(basketItemToSave.getProductCount() + numberToAdd);
            basketItemToSave.setOriginalPrice(product.getOriginalPrice());
            basketItemToSave.setDiscountPercentage(product.getDiscountPercentage());
            basketItemToSave.setUpdateTime(LocalDate.now());

        } else {
            basketItemToSave = new BasketItem(productId, customerId, numberToAdd, product.getOriginalPrice(), product.getDiscountPercentage());
        }
        basketItemRepository.save(basketItemToSave);
    }
}
