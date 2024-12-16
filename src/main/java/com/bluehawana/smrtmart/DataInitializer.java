package com.bluehawana.smrtmart;

import com.bluehawana.smrtmart.model.*;
import com.bluehawana.smrtmart.repository.*;
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
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

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

    private static final List<InitialUser> INITIAL_USERS = Arrays.asList(
            new InitialUser("developer1@example.com", "developer1", Role.USER),
            new InitialUser("developer2@example.com", "developer2", Role.USER),
            new InitialUser("lee@bluehawana.com", "admin", Role.ADMIN)
    );

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Starting data initialization check...");
            initializeData();
            log.info("Data initialization check completed.");
        };
    }

    @Transactional
    public void initializeData() {
        try {
            initializeProducts(); // Initialize products first (needed for cart items)
            initializeUsers();    // Then users and their carts
            log.info("Data initialization successful");
        } catch (Exception e) {
            log.error("Error during data initialization", e);
            throw e;
        }
    }

    private void initializeUsers() {
        Set<String> existingEmails = new HashSet<>();
        userRepository.findAll().forEach(user -> existingEmails.add(user.getEmail()));

        for (InitialUser init : INITIAL_USERS) {
            if (!existingEmails.contains(init.email())) {
                User user = createUser(init);
                user = userRepository.save(user);
                createCartForUser(user);
                log.info("Created new user: {}", init.email());
            } else {
                log.debug("User already exists: {}", init.email());
            }
        }
    }

    private void initializeProducts() {
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

    private User createUser(InitialUser init) {
        User user = new User();
        user.setEmail(init.email());
        user.setUsername(init.username());
        user.setPassword("password"); // In production, use passwordEncoder
        user.setRole(init.role());
        user.setEnabled(true);
        return user;
    }

    private void createCartForUser(User user) {
        if (!cartRepository.existsByUserId(user.getId())) {
            Cart cart = new Cart();
            cart.setUserId(Long.valueOf(user.getId()));

            // Save cart first
            cart = cartRepository.save(cart);

            // Add some sample products to cart
            List<Product> products = productRepository.findAll();
            if (!products.isEmpty()) {
                // Add MacBook Pro to first user's cart
                if (user.getEmail().equals("developer1@example.com") && products.size() >= 1) {
                    CartItem item1 = new CartItem();
                    item1.setCart(cart);
                    item1.setProduct(products.get(0));  // MacBook Pro
                    item1.setQuantity(1);
                    cart.getItems().add(item1);
                }

                // Add AirPods to second user's cart
                if (user.getEmail().equals("developer2@example.com") && products.size() >= 2) {
                    CartItem item2 = new CartItem();
                    item2.setCart(cart);
                    item2.setProduct(products.get(1));  // AirPods
                    item2.setQuantity(2);
                    cart.getItems().add(item2);
                }
            }

            cartRepository.save(cart);
            log.info("Created cart with {} items for user: {}",
                    cart.getItems().size(), user.getEmail());
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
        initializeData();
    }
}

// Record classes for initial data
record InitialProduct(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl
) {}

record InitialUser(
        String email,
        String username,
        Role role
) {}