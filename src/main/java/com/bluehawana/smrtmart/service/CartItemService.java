package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.model.CartItem;
import com.bluehawana.smrtmart.model.Product;
import com.bluehawana.smrtmart.repository.CartItemRepository;
import com.bluehawana.smrtmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItemDTO addItem(CartItemDTO itemDTO) {
        log.info("Adding item to cart: {}", itemDTO);

        // 查找商品信息
        Product product = productRepository.findById(Long.valueOf(itemDTO.getProductId()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // 检查是否已存在该商品
        CartItem existingItem = cartItemRepository.findByProductId(itemDTO.getProductId());

        if (existingItem != null) {
            // 如果存在，更新数量
            existingItem.setQuantity(existingItem.getQuantity() + itemDTO.getQuantity());
            return convertToDTO(cartItemRepository.save(existingItem));
        }

        // 如果不存在，创建新的购物车项
        CartItem newItem = new CartItem();
        newItem.setProductId(itemDTO.getProductId());
        newItem.setName(product.getName());
        newItem.setPrice(product.getPrice());
        newItem.setQuantity(itemDTO.getQuantity());

        return convertToDTO(cartItemRepository.save(newItem));
    }

    public CartItemDTO updateQuantity(Long itemId, Integer quantity) {
        log.info("Updating quantity for item {}: {}", itemId, quantity);

        CartItem item = cartItemRepository.findById(Math.toIntExact(itemId))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }

        item.setQuantity(quantity);
        return convertToDTO(cartItemRepository.save(item));
    }

    public void removeItem(Long itemId) {
        log.info("Removing item from cart: {}", itemId);
        if (!cartItemRepository.existsById(Math.toIntExact(itemId))) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        cartItemRepository.deleteById(Math.toIntExact(itemId));
    }

    public List<CartItemDTO> getAllItems() {
        return cartItemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CartItemDTO convertToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setName(item.getName());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}