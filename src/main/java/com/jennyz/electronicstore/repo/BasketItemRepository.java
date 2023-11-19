package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.Basket;
import com.jennyz.electronicstore.Entity.BasketItem;
import com.jennyz.electronicstore.Entity.BasketItemId;
import com.jennyz.electronicstore.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BasketItemRepository extends JpaRepository<BasketItem, BasketItemId> {

    @Query("SELECT a FROM BasketItem a WHERE a.customerId = :customerId")
    List<BasketItem> findAllByCustomerId(Long customerId);


}

