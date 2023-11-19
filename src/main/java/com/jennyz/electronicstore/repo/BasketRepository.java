package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.Basket;
import com.jennyz.electronicstore.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket,Long> {
}
