package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.Cart;
import com.bluehawana.smrtmart.model.CartItem;
import com.bluehawana.smrtmart.model.Product;
import com.bluehawana.smrtmart.repository.CartItemRepository;
import com.bluehawana.smrtmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItem addItemToCart(Cart cart, CartItemDTO itemDTO) {
        Product product = productRepository.findById(Long.valueOf(itemDTO.getProductId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(itemDTO.getProductId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.setQuantity(itemDTO.getQuantity());
        return cartItemRepository.save(cartItem);
    }

    public void removeItem(Cart cart, Integer itemId) {
        cartItemRepository.deleteById(itemId);
    }

    public CartItem updateQuantity(Integer itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    public void updateItemQuantity(Cart cart, Integer itemId, Integer quantity) {
    }

    public List<CartItemDTO> convertCartItemsToDTO(List<CartItem> items) {
        return items.stream()
                .map(this::convertSingleItemToDTO)
                .collect(Collectors.toList());
    }

    private CartItemDTO convertSingleItemToDTO(CartItem item) {
        // Conversion logic here
        return new CartItemDTO();
    }

    public List<CartItemDTO> convertToDTO(List<CartItem> items) {
        return items.stream()
                .map(this::convertSingleItemToDTO)
                .collect(Collectors.toList());
    }
}
