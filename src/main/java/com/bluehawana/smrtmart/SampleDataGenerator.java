package com.bluehawana.smrtmart;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.bluehawana.smrtmart.model.*;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Transactional
public class SampleDataGenerator {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void generateData() {
        // Check if data already exists
        if (!entityManager.createQuery("SELECT p FROM Product p").getResultList().isEmpty()) {
            log.info("Data already exists, skipping generation");
            return;
        }

        try {
            // Create sample products
            Product macbookPro = new Product();
            macbookPro.setName("MacBook Pro");
            macbookPro.setDescription("Apple MacBook Pro 16-inch, M1 Max");
            macbookPro.setPrice(new BigDecimal("3499.99"));
            macbookPro.setStockQuantity(50);
            macbookPro.setImageUrl("url_to_macbook_image");

            Product airpods = new Product();
            airpods.setName("AirPods Pro");
            airpods.setDescription("Apple AirPods Pro with Active Noise Cancellation");
            airpods.setPrice(new BigDecimal("249.99"));
            airpods.setStockQuantity(200);
            airpods.setImageUrl("url_to_airpods_image");

            Product sonyWH1000XM5 = new Product();
            sonyWH1000XM5.setName("Sony WH-1000XM5");
            sonyWH1000XM5.setDescription("Sony WH-1000XM5 Wireless Industry Leading Noise Canceling Overhead Headphones");
            sonyWH1000XM5.setPrice(new BigDecimal("399.99"));
            sonyWH1000XM5.setStockQuantity(150);
            sonyWH1000XM5.setImageUrl("url_to_sony_headphones_image");

            Product dellXPS13 = new Product();
            dellXPS13.setName("Dell XPS 13");
            dellXPS13.setDescription("Dell XPS 13 9310 13.4-inch FHD+ Touchscreen Laptop");
            dellXPS13.setPrice(new BigDecimal("1299.99"));
            dellXPS13.setStockQuantity(100);
            dellXPS13.setImageUrl("url_to_dell_laptop_image");

            Product samsungGalaxyS21 = new Product();
            samsungGalaxyS21.setName("Samsung Galaxy S21");
            samsungGalaxyS21.setDescription("Samsung Galaxy S21 5G Factory Unlocked Android Cell Phone");
            samsungGalaxyS21.setPrice(new BigDecimal("799.99"));
            samsungGalaxyS21.setStockQuantity(300);
            samsungGalaxyS21.setImageUrl("url_to_samsung_phone_image");

            Product iphone13promax = new Product();
            iphone13promax.setName("iPhone 13 Pro Max");
            iphone13promax.setDescription("Apple iPhone 13 Pro Max, 128GB, Graphite - Fully Unlocked");
            iphone13promax.setPrice(new BigDecimal("1099.99"));
            iphone13promax.setStockQuantity(100);
            iphone13promax.setImageUrl("url_to_iphone_image");



            entityManager.persist(macbookPro);
            entityManager.persist(airpods);
            entityManager.persist(sonyWH1000XM5);
            entityManager.persist(dellXPS13);
            entityManager.persist(samsungGalaxyS21);
            entityManager.persist(iphone13promax);
            entityManager.flush();

            // Create sample users
            User user1 = new User();
            user1.setEmail("developer1@example.com");
            user1.setRole("USER");
            user1.setEnabled(true);

            User user2 = new User();
            user2.setEmail("developer2@example.com");
            user2.setRole("USER");
            user2.setEnabled(true);

            User user3 = new User();
            user3.setEmail("lee@bluehawana.com");
            user3.setRole("ADMIN");

            entityManager.persist(user1);
            entityManager.persist(user2);
            entityManager.persist(user3);
            entityManager.flush();

            // Create sample carts
            Cart cart1 = new Cart();
            cart1.setUser(user1);
            entityManager.persist(cart1);

            CartItem cartItem1 = new CartItem();
            cartItem1.setCart(cart1);
            cartItem1.setProduct(macbookPro);
            cartItem1.setQuantity(1);
            entityManager.persist(cartItem1);

            List<CartItem> cartItems1 = new ArrayList<>();
            cartItems1.add(cartItem1);
            cart1.setItems(cartItems1);

            Cart cart2 = new Cart();
            cart2.setUser(user2);
            entityManager.persist(cart2);

            CartItem cartItem2 = new CartItem();
            cartItem2.setCart(cart2);
            cartItem2.setProduct(airpods);
            cartItem2.setQuantity(2);
            entityManager.persist(cartItem2);

            List<CartItem> cartItems2 = new ArrayList<>();
            cartItems2.add(cartItem2);
            cart2.setItems(cartItems2);

            entityManager.flush();

            // Create sample orders
            Order order1 = new Order();
            order1.setUser(user1);
            order1.setStatus("COMPLETED");
            order1.setTotalAmount(cartItem1.getTotalPrice());
            order1.setShippingAddress("123 Developer Lane");
            order1.setStripePaymentId("stripe_payment_id_1");
            order1.setCreatedAt(LocalDateTime.now());
            order1.setUpdatedAt(LocalDateTime.now());
            entityManager.persist(order1);

            OrderItem orderItem1 = new OrderItem(macbookPro, cartItem1.getQuantity());
            orderItem1.setOrder(order1);
            orderItem1.setPriceAtTime(macbookPro.getPrice());
            entityManager.persist(orderItem1);

            List<OrderItem> orderItems1 = new ArrayList<>();
            orderItems1.add(orderItem1);
            order1.setItems(orderItems1);

            Order order2 = new Order();
            order2.setUser(user2);
            order2.setStatus("COMPLETED");
            order2.setTotalAmount(cartItem2.getTotalPrice());
            order2.setShippingAddress("456 Developer Blvd");
            order2.setStripePaymentId("stripe_payment_id_2");
            order2.setCreatedAt(LocalDateTime.now());
            order2.setUpdatedAt(LocalDateTime.now());
            entityManager.persist(order2);

            OrderItem orderItem2 = new OrderItem(airpods, cartItem2.getQuantity());
            orderItem2.setOrder(order2);
            orderItem2.setPriceAtTime(airpods.getPrice());
            entityManager.persist(orderItem2);

            List<OrderItem> orderItems2 = new ArrayList<>();
            orderItems2.add(orderItem2);
            order2.setItems(orderItems2);

            entityManager.flush();
            log.info("Sample data generated successfully");
        } catch (Exception e) {
            log.error("Error generating sample data", e);
            throw e;
        }
    }
}