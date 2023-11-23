package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.dto.BasketInfo;
import com.jennyz.electronicstore.entity.BasketItem;
import com.jennyz.electronicstore.entity.BasketItemId;
import com.jennyz.electronicstore.entity.Product;
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

@Service
public class BasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasketService.class);
    private final int maxRetries;
    private final BasketItemRepository basketItemRepository;

    private final ProductService productService;

    public BasketService(BasketItemRepository basketItemRepository, ProductService productService, @Value("$" +
            "{electronicstore.basket.retry.time}") int retry) {
        this.basketItemRepository = basketItemRepository;
        this.productService = productService;
        this.maxRetries = retry;
    }

    public List<BasketItem> findBasketItemsByCustomer(Long customerId) {
        LOGGER.info("find basket item list by customerId {}", customerId);
        return basketItemRepository.findAllByCustomerId(customerId);

    }

    @Transactional
    public BasketItem addItemsToBasket(Long productId, Long customerId, int numberToAdd) {
        LOGGER.info("start to add {} product {} to basket for customer {}", numberToAdd, productId, customerId);
        BasketItem basketItem = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                Product product =
                        productService.findProduct(productId).orElseThrow(() -> new ProductNotFoundException(
                                String.format("product to add %s not exist", productId)));

                int currentStock = product.getStockNum();
                if (numberToAdd > currentStock) {
                    throw new NotEnoughStockException("Not enough stock exist");
                }
                productService.updateProductStockNum(product, currentStock - numberToAdd);
                basketItem = createOrUpdateBasketItem(product, customerId, numberToAdd);
                break; // Break out of loop if successful
            } catch (OptimisticLockException e) {
                LOGGER.error("Data conflict encountered when add product from basket", e);
                // If it's the last iteration, throw the exception. Otherwise, continue to the next iteration.
                if (i == maxRetries - 1) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Data conflict after multiple retries.");
                }
            }
        }

        return basketItem;
    }


    @Transactional
    public BasketItem removeItemsFromBasket(Long productId, Long customerId, int numberToRemove) {
        LOGGER.info("start to remove {} product {} from basket for {}", numberToRemove, productId, customerId);
        BasketItem basketItemToReturn = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                BasketItem basketItem =
                        basketItemRepository.findById(new BasketItemId(productId, customerId)).orElseThrow(() ->
                                new BasketItemNotFoundException("basketItem not found"));

                int itemNum = basketItem.getProductCount();
                if (itemNum < numberToRemove) {
                    throw new IllegalArgumentException("not enough basket items to be removed");
                }
                Optional<Product> productOpt =
                        productService.findProduct(productId);
                if (productOpt.isEmpty()) {
                    LOGGER.warn("product {} not exist but item exist for customer {}", productId, customerId);
                } else {
                    productService.updateProductStockNum(productOpt.get(),
                            productOpt.get().getStockNum() + numberToRemove);
                }

                if (itemNum == numberToRemove) {
                    basketItemRepository.delete(basketItem);
                } else {
                    basketItem.setProductCount(itemNum - numberToRemove);
                    basketItemToReturn = basketItemRepository.save(basketItem);
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
        return basketItemToReturn;
    }

    public BasketInfo getBasketInfo(Long customerId) {
        List<BasketItem> list = findBasketItemsByCustomer(customerId);
        if (Objects.isNull(list) || list.isEmpty()) {
            return new BasketInfo(new ArrayList<>(), customerId, 0.0);
        }
        double totalPrice = getTotalPrice(list);
        return new BasketInfo(list, customerId, totalPrice);
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

    private BasketItem createOrUpdateBasketItem(Product product, Long customerId, int numberToAdd) {
        Long productId = product.getId();
        Optional<BasketItem> basketItem = basketItemRepository.findById(new BasketItemId(productId, customerId));
        BasketItem newBasketItem;
        if (basketItem.isPresent()) {
            newBasketItem = basketItem.get();
            newBasketItem.setProductCount(newBasketItem.getProductCount() + numberToAdd);
            newBasketItem.setOriginalPrice(product.getOriginalPrice());
            newBasketItem.setDiscountPercentage(product.getDiscountPercentage());
            newBasketItem.setUpdateTime(LocalDate.now());

        } else {
            newBasketItem = new BasketItem(productId, customerId, numberToAdd, product.getOriginalPrice(),
                    product.getDiscountPercentage());
        }
        return basketItemRepository.save(newBasketItem);
    }

}
