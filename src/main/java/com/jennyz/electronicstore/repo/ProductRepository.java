package com.jennyz.electronicstore.repo;

import com.jennyz.electronicstore.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT a FROM Product a WHERE a.productName = :productName")
    Product findByName(String productName);



    /*@Modifying
    @Transactional
    @Query(value = "update Product a set a.stockNum = :stockNum, a.version = version + 1 where a.id = :productId and a.version = :version")
    int updateProductStockWithVersion(Long productId, int stockNum, int version);*/
}
