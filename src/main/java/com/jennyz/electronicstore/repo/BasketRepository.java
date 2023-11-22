package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.entity.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket,Long> {
}
