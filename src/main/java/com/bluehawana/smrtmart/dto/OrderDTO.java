package com.bluehawana.smrtmart.dto;

import com.bluehawana.smrtmart.model.Order;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDTO {
    private Integer id;
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;

    public OrderDTO(Order save) {
    }

    public OrderDTO(Integer id, String status, BigDecimal totalAmount, String shippingAddress, List<OrderItemDTO> items, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.createdAt = createdAt;
    }
}
