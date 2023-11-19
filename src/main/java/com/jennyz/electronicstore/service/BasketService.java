package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.BasketItemId;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.exception.BasketItemNotFoundException;
import com.jennyz.electronicstore.exception.NotEnoughStockException;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.BasketItemRepository;
import com.jennyz.electronicstore.repo.BasketRepository;
import com.jennyz.electronicstore.repo.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BasketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasketService.class);
    private final int retry;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final BasketRepository basketRepository;

    private final BasketItemRepository basketItemRepository;

    private final ProductService productService;

    public BasketService(BasketRepository basketRepository, BasketItemRepository basketItemRepository, ProductService productService, @Value("${electronicstore.basket.retry.time}") int retry) {
        this.basketRepository = basketRepository;
        this.basketItemRepository = basketItemRepository;
        this.productService = productService;
        this.retry = retry;
    }

    public List<BasketItem> getItemsByCustomer(Long customerId) {
        return basketItemRepository.findAllByCustomerId(customerId);

    }


    public void addProductInBasket(Long productId, Long customerId, int number) throws InterruptedException {
        LOGGER.info("start to add {} product {} to basket for {}", number, productId, customerId);
        int retryTime = retry;
    //    while (retryTime-- > 0) {
            Product product = productService.findProduct(productId)
                    .orElseThrow(() -> new ProductNotFoundException(String.format("product to add {} not exist", productId)));

            int currentStock = product.getStockNum();
            int version = product.getVersion();
            if (number > currentStock) {
                throw new NotEnoughStockException("Not enough stock exist");
            }
            Optional<BasketItem> basketItem = basketItemRepository.findById(new BasketItemId(productId, customerId));

            BasketItem basketItemToSave;
            if (basketItem.isPresent()) {
                basketItemToSave = basketItem.get();
                basketItemToSave.setProductCount(basketItemToSave.getProductCount() + number);
                basketItemToSave.setOriginalPrice(product.getOriginalPrice());
                basketItemToSave.setDiscountPercentage(product.getDiscountPercentage());
                basketItemToSave.setUpdateTime(LocalDate.now());

            } else {
                basketItemToSave = new BasketItem(productId, customerId, number, product.getOriginalPrice(), product.getDiscountPercentage());
            }
            product.setStockNum(currentStock - number);
            LOGGER.info("current time is {}", LocalDateTime.now());
            Thread.sleep(10000);
        LOGGER.info("current time is {} after sleep product version is {} ", LocalDateTime.now(), product.getVersion());
            productService.save(product);
            basketItemRepository.save(basketItemToSave);



    }

    public void removeProductFromBasket(Long productId, Long customerId, int number) {
        LOGGER.info("start to remove {} product {} from basket for {}", number, productId, customerId);
        Product product = productService.findProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("product to remove {} not exist", productId)));

        BasketItem basketItem = basketItemRepository.findById(new BasketItemId(productId, customerId))
                .orElseThrow(() -> new BasketItemNotFoundException("basketItem not found"));

        int itemNum = basketItem.getProductCount();
        if (itemNum < number) {
            throw new IllegalArgumentException("not enough basket items to be removed");
        }

        if (itemNum == number) {
            basketItemRepository.delete(basketItem);
        } else {
            basketItem.setProductCount(itemNum - number);
            basketItemRepository.save(basketItem);
        }
        product.setStockNum(product.getStockNum() + number);
        productService.save(product);

    }

    public Double calculateBasketPrice(Long customerId) {
        List<BasketItem> list = basketItemRepository.findAllByCustomerId(customerId);
        double totalPrice = 0.0;
        if (Objects.isNull(list) || list.isEmpty()) {
            return totalPrice;
        }
        for (BasketItem basketItem : list) {
            int num = basketItem.getProductCount();
            Double originalPrice = basketItem.getOriginalPrice();
            int percentage = basketItem.getDiscountPercentage();

            if(percentage == 0 ){
                totalPrice += num * originalPrice;
                continue;
            }

            double ratio = 1.0 - percentage/100.0;
            if(num % 2 == 0 ){
                //second one get percentage discount
                totalPrice += num * originalPrice * (1 + ratio)/2;
            }else{
                totalPrice += (num - 1) * originalPrice * (1 + ratio)/2 + originalPrice;
            }

        }
        return totalPrice;

    }
}
