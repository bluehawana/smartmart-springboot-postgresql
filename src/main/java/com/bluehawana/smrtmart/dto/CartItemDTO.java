package com.bluehawana.smrtmart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private String productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Product product;

    public String getDescription() {
        return name + (product != null ? product.getDescription() : "") + quantity;
    }
}