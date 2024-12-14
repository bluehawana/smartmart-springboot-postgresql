package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Cart;
import com.bluehawana.smrtmart.model.CartItem;
import com.bluehawana.smrtmart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public CartDTO getCurrentCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));
        return convertToDTO(cart);
    }

    private Cart createNewCart(Integer userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .items(itemDTOs)
                .totalAmount(calculateTotal(cart))
                .build();
    }

    private CartItemDTO convertToCartItemDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(Math.toIntExact(item.getProduct().getId()))
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

    public CartDTO addToCart(Integer userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        cartItemService.addItemToCart(cart, cartItemDTO);
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    public CartDTO updateCartItem(Integer userId, Integer itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemService.updateItemQuantity(cart, itemId, quantity);
        cart = cartRepository.save(cart);

        return convertToDTO(cart);
    }

    public void removeFromCart(Integer userId, Integer itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemService.removeItem(cart, itemId);
        cartRepository.save(cart);
    }

    public void clearCart(Integer userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}