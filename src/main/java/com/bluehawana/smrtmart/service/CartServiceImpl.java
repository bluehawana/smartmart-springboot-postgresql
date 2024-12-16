package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Cart;
import com.bluehawana.smrtmart.model.CartItem;
import com.bluehawana.smrtmart.model.Product;
import com.bluehawana.smrtmart.repository.CartRepository;
import com.bluehawana.smrtmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getCurrentCart(Long userId) {
        Cart cart = cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseGet(() -> createNewCart(userId));
        return mapToDTO(cart);
    }

    @Override
    public CartDTO addToCart(Long userId, CartItemDTO cartItemDTO) {
        Cart cart = cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseGet(() -> createNewCart(userId));

        Product product = productRepository.findById(cartItemDTO.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if product already in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItemDTO.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(cartItemDTO.getQuantity());
            cart.addItem(newItem);
        }

        cart = cartRepository.save(cart);
        return mapToDTO(cart);
    }

    @Override
    public CartDTO updateCartItem(Long userId, Long itemId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity > 0) {
            item.setQuantity(quantity);
        } else {
            cart.removeItem(item);
        }

        cart = cartRepository.save(cart);
        return mapToDTO(cart);
    }

    @Override
    public void removeFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cart.removeItem(item);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(Math.toIntExact(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private CartDTO mapToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());
        dto.setItems(cart.getItems().stream()
                .map(this::mapToCartItemDTO)
                .toList());
        return dto;
    }

    private CartItemDTO mapToCartItemDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}
