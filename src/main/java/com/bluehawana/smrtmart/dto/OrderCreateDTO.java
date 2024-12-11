package com.bluehawana.smrtmart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCreateDTO {
    private String shippingAddress;
    private String paymentMethod;

    public BigDecimal getTotalAmount() {
        return BigDecimal.ZERO;
    }
}