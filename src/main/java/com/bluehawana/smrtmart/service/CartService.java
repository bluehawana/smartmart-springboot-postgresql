package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;

public interface CartService {
    CartDTO getCurrentCart(Long userId);
    CartDTO addToCart(Long userId, CartItemDTO cartItem);
    CartDTO updateCartItem(Long userId, Long itemId, Integer quantity);
    void removeFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
}