package com.bluehawana.smrtmart;

import com.bluehawana.smrtmart.model.Product;
import com.bluehawana.smrtmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataInitializer {

    private final ProductRepository productRepository;

    private static final List<InitialProduct> INITIAL_PRODUCTS = Arrays.asList(
            new InitialProduct(
                    "MacBook Pro",
                    "Apple MacBook Pro 16-inch, M1 Max",
                    new BigDecimal("3499.99"),
                    50,
                    "/api/uploads/macbook.jpg"
            ),
            new InitialProduct(
                    "AirPods Pro2",
                    "Apple AirPods Pro with Active Noise Cancellation",
                    new BigDecimal("249.99"),
                    200,
                    "/api/uploads/airpods.jpg"
            ),
            new InitialProduct(
                    "Sony WH-1000XM5",
                    "Sony WH-1000XM5 Wireless Industry Leading Noise Canceling Overhead Headphones",
                    new BigDecimal("399.99"),
                    150,
                    "/api/uploads/sony.jpg"
            ),
            new InitialProduct(
                    "Dell XPS 13",
                    "Dell XPS 13 9310 13.4-inch FHD+ Touchscreen Laptop",
                    new BigDecimal("1299.99"),
                    100,
                    "/api/uploads/dell.jpg"
            ),
            new InitialProduct(
                    "Dell Alienware 34 Screen",
                    "Dell Alienware 34-inch Curved Gaming Monitor",
                    new BigDecimal("1499.99"),
                    100,
                    "/api/uploads/dell.jpg"
            ),
            new InitialProduct(
                    "Apple Watch Ultra 2",
                    "Apple Watch Ultra 2, GPS + Cellular, 49mm Titanium Case",
                    new BigDecimal("799.99"),
                    100,
                    "/api/uploads/ultra2.jpg"
            )
    );

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting product initialization...");
            initializeProducts();
            log.info("Product initialization completed.");
        };
    }

    @Transactional
    public void initializeProducts() {
        Set<String> existingProducts = new HashSet<>();
        productRepository.findAll().forEach(product -> existingProducts.add(product.getName()));

        for (InitialProduct init : INITIAL_PRODUCTS) {
            if (!existingProducts.contains(init.name())) {
                Product product = createProduct(init);
                productRepository.save(product);
                log.info("Created new product: {}", init.name());
            } else {
                log.debug("Product already exists: {}", init.name());
            }
        }
    }

    private Product createProduct(InitialProduct init) {
        Product product = new Product();
        product.setName(init.name());
        product.setDescription(init.description());
        product.setPrice(init.price());
        product.setStockQuantity(init.stockQuantity());
        product.setImageUrl(init.imageUrl());
        return product;
    }

    public void generateData() {
    }
}

// Record class for initial product data
record InitialProduct(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl
) {}