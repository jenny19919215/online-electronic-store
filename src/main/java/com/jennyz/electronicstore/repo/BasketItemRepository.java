package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.entity.BasketItem;
import com.jennyz.electronicstore.entity.BasketItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BasketItemRepository extends JpaRepository<BasketItem, BasketItemId> {

    @Query("SELECT a FROM BasketItem a WHERE a.customerId = :customerId")
    List<BasketItem> findAllByCustomerId(Long customerId);


}

