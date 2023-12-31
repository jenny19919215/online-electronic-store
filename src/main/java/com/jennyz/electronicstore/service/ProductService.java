package com.jennyz.electronicstore.service;

import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.exception.ProductAlreadyExist;
import com.jennyz.electronicstore.exception.ProductNotFoundException;
import com.jennyz.electronicstore.repo.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProduct(Long id) {
        LOGGER.info("start to find product by id {}", id);
        if(id == null){
            throw new IllegalArgumentException("product id must not be null");
        }
        return productRepository.findById(id);
    }

    @Transactional
    public Product insertProduct(Product product) {
        LOGGER.info("insert Product {}", product);
        if (productRepository.findByName(product.getProductName()) != null) {
            throw new ProductAlreadyExist("Product with name " + product.getProductName() + " already exist");
        }
        return productRepository.save(product);
    }

    @Transactional
    public void updateProductDiscountInfo(Long productId, int percentage) {
        LOGGER.info("update product by Id {} with percentage {}", productId, percentage);
        Product product = findProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("product %s not exist", productId)));
        product.setDiscountPercentage(percentage);
        productRepository.save(product);
    }


    public void deleteProduct(Long productId) {
        LOGGER.info("delete product by Id {}", productId);
        if(productId == null){
            throw new IllegalArgumentException("product id should not be null");
        }

        try {
            productRepository.deleteById(productId);
        }catch (EmptyResultDataAccessException ex){
            throw new ProductNotFoundException("Product not found");
        }
        LOGGER.info("product {} has been deleted", productId);
    }

    @Transactional
    public void updateProductStockNum(Product product, int num) {
        LOGGER.info("update product {} with stock num {}", product, num);
        if (Objects.isNull(product)) {
            throw new ProductNotFoundException("product to save is null");
        }
        if (num < 0) {
            throw new IllegalArgumentException("product stock num should be positive");
        }

        product.setStockNum(num);
        productRepository.save(product);
    }
}
