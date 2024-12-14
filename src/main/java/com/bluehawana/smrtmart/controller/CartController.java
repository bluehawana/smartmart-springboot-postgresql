package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.dto.CartUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getCurrentCart() {
        log.info("Getting current cart");
        try {
            CartDTO cart = cartService.getCurrentCart(getCurrentUserId());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error getting cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@RequestBody CartItemDTO cartItem) {
        log.info("Adding item to cart: {}", cartItem);
        try {
            CartDTO updatedCart = cartService.addToCart(getCurrentUserId(), cartItem);
            return ResponseEntity.ok(updatedCart);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error adding to cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Integer itemId,
            @RequestBody CartUpdateDTO update) {
        log.info("Updating cart item: {}", itemId);
        try {
            CartDTO updatedCart = cartService.updateCartItem(
                    getCurrentUserId(),
                    itemId,
                    update.getQuantity()
            );
            return ResponseEntity.ok(updatedCart);
        } catch (ResourceNotFoundException e) {
            log.error("Cart item not found", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating cart item", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer itemId) {
        log.info("Removing item from cart: {}", itemId);
        try {
            cartService.removeFromCart(getCurrentUserId(), itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error removing from cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        log.info("Clearing cart");
        try {
            cartService.clearCart(getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Helper method to get current user ID
    private Integer getCurrentUserId() {
        // TODO: Replace with actual user authentication logic
        return 1; // Temporary default user ID
    }
}