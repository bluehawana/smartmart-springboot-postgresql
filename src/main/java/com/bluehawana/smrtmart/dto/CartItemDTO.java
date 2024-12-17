package com.bluehawana.smrtmart.dto;

import lombok.Data;
import java.math.BigDecimal;
import com.bluehawana.smrtmart.model.Product;

@Data
public class CartItemDTO {
    private Long id;
    private Long productId;  // 改为 Long 类型
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private Product product;

    // 添加一个用于前端显示的方法
    public String getDescription() {
        return name + (product != null ? product.getDescription() : "") + quantity;
    }
}