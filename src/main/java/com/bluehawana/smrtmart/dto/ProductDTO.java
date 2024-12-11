package com.bluehawana.smrtmart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Integer id;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String name;

    public static ProductDTO builder() {
        return new ProductDTO();
    }

    public ProductDTO id(Integer id) {
        this.id = id;
        return this;
    }

    public ProductDTO name(String name) {
        this.name = name;
        return this;
    }

    public ProductDTO description(String description) {
        this.description = description;
        return this;
    }

    public ProductDTO price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductDTO stockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
        return this;
    }

    public ProductDTO imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public ProductDTO build() {
        return this;
    }
}