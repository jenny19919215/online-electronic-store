package com.jennyz.electronicstore;

import com.jennyz.electronicstore.utils.Category;
import com.jennyz.electronicstore.Entity.Product;
import com.jennyz.electronicstore.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ElectronicStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(ProductRepository productRepository) {
		return args -> {
			Product product = new Product("phone",100.0,100.0,20, Category.MOBILES, 1L) ;
			productRepository.save(product);
		};
	}

}
