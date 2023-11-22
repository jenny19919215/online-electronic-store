package com.jennyz.electronicstore;

import com.jennyz.electronicstore.entity.Product;
import com.jennyz.electronicstore.repo.ProductRepository;
import com.jennyz.electronicstore.utils.Category;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
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

	@Bean
	public OpenAPI customOpenAPI(@Value("${onlinestore.api.name}") String name,
								 @Value("${onlinestore.api.version}") String version) {
		return new OpenAPI()
				.info(new Info()
						.title(name)
						.version(version));
	}

}
