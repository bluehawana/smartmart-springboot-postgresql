package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.dto.CartUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CartController {
    private final CartItemService cartItemService;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart() {
        log.info("GET /api/cart - Fetching all cart items");
        return ResponseEntity.ok(cartItemService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO item) {
        log.info("POST /api/cart/items - Adding item to cart: {}", item);
        try {
            if (item.getQuantity() <= 0) {
                return ResponseEntity.badRequest().body("Quantity must be greater than 0");
            }
            CartItemDTO added = cartItemService.addItem(item);
            return ResponseEntity.ok(added);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to add item to cart: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error adding item to cart", e);
            return ResponseEntity.badRequest().body("Failed to add item to cart");
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody CartUpdateDTO update) {
        log.info("PUT /api/cart/items/{} - Updating quantity to: {}", itemId, update.getQuantity());
        try {
            if (update.getQuantity() < 0) {
                return ResponseEntity.badRequest().body("Quantity cannot be negative");
            }
            CartItemDTO updated = cartItemService.updateQuantity(itemId, update.getQuantity());
            if (updated == null) {
                // Item was removed because quantity was 0
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            log.error("Cart item not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId) {
        log.info("DELETE /api/cart/items/{} - Removing item from cart", itemId);
        try {
            cartItemService.removeItem(itemId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.error("Failed to remove item: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        log.info("DELETE /api/cart - Clearing cart");
        try {
            cartItemService.getAllItems().forEach(item ->
                    cartItemService.removeItem(item.getId())
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.badRequest().body("Failed to clear cart");
        }
    }
}