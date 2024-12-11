package com.bluehawana.smrtmart.dto;

import com.bluehawana.smrtmart.model.OrderItem;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDTO {
    private Integer id;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtTime;

    public OrderItemDTO id(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.productName = orderItem.getProduct().getName();
        this.quantity = orderItem.getQuantity();
        this.priceAtTime = orderItem.getPriceAtTime();
        return this;
    }
}