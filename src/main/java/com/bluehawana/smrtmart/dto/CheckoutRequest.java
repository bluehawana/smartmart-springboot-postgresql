// src/main/java/com/bluehawana/smrtmart/dto/CheckoutRequest.java
package com.bluehawana.smrtmart.dto;

import com.bluehawana.smrtmart.model.CartItem;
import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
    private List<CartItem> items;
}