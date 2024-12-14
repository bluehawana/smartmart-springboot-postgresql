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

    public static OrderItemDTO fromOrderItem(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .priceAtTime(orderItem.getPriceAtTime())
                .build();
    }
}