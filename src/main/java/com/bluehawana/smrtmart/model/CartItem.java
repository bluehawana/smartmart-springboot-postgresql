package com.bluehawana.smrtmart.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}