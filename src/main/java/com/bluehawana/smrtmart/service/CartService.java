package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.dto.CartUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.*;
import com.bluehawana.smrtmart.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartDTO getCurrentCart() {
        // For testing, using user ID 1. In production, get from security context
        Cart cart = cartRepository.findByUserId(1)
                .orElseGet(() -> createNewCart(1));
        return convertToDTO(cart);
    }

    public CartDTO addToCart(CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserId(1)
                .orElseGet(() -> createNewCart(1));

        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemDTO.getProductId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.setQuantity(cartItemDTO.getQuantity());
        cartRepository.save(cart);
        return convertToDTO(cart);
    }

    public void removeFromCart(Integer itemId) {
        cartItemRepository.findById(itemId)
                .ifPresent(cartItemRepository::delete);
    }

    private Cart createNewCart(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .items(cart.getItems().stream()
                        .map(this::convertToCartItemDTO)
                        .collect(Collectors.toList()))
                .totalAmount(calculateTotal(cart))
                .build();
    }

    private CartItemDTO convertToCartItemDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice())
                .build();
    }

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public CartDTO updateCartItem(Integer itemId, CartUpdateDTO update) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItem.setQuantity(update.getQuantity());
        cartItemRepository.save(cartItem);
        return convertToDTO(cartItem.getCart());
    }

    public void clearCart() {
        cartRepository.findByUserId(1)
                .ifPresent(cart -> {
                    cart.getItems().clear();
                    cartRepository.save(cart);
                });
    }
}