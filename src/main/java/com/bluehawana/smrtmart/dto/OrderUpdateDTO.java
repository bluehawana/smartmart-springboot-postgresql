package com.bluehawana.smrtmart.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderUpdateDTO {
    private String status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String stripePaymentId;
}
