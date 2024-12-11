// File: src/main/java/com/bluehawana/smrtmart/dto/CartDTO.java
package com.bluehawana.smrtmart.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartDTO {
    private Integer id;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
}
