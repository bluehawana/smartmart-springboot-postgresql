package com.bluehawana.smrtmart.model;

import com.stripe.model.HasId;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
public class Product implements HasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;

    @Override
    public String getId() {
        return String.valueOf(id);
    }
}