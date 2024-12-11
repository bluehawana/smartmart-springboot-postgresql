package com.bluehawana.smrtmart.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CartItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
