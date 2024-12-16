package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.dto.CartUpdateDTO;
import com.bluehawana.smrtmart.exception.ResourceNotFoundException;
import com.bluehawana.smrtmart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")  // Updated to match your API pattern
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
        } catch (ResourceNotFoundException e) {
            log.warn("Cart not found for user {}", getCurrentUserId());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting cart for user {}", getCurrentUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO cartItem) {
        log.info("Adding item to cart: {}", cartItem);
        try {
            if (cartItem.getQuantity() <= 0) {
                return ResponseEntity.badRequest()
                        .body("Quantity must be greater than 0");
            }

            CartDTO updatedCart = cartService.addToCart(getCurrentUserId(), cartItem);
            return ResponseEntity.ok(updatedCart);
        } catch (ResourceNotFoundException e) {
            log.warn("Product not found: {}", cartItem.getProductId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Product not found");
        } catch (Exception e) {
            log.error("Error adding item to cart: {}", cartItem, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to add item to cart");
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody CartUpdateDTO update) {
        log.info("Updating cart item: {} with quantity: {}", itemId, update.getQuantity());
        try {
            if (update.getQuantity() < 0) {
                return ResponseEntity.badRequest()
                        .body("Quantity cannot be negative");
            }

            CartDTO updatedCart = cartService.updateCartItem(
                    getCurrentUserId(),
                    (long) Math.toIntExact(itemId),
                    update.getQuantity()
            );
            return ResponseEntity.ok(updatedCart);
        } catch (ResourceNotFoundException e) {
            log.warn("Cart item not found: {}", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cart item not found");
        } catch (Exception e) {
            log.error("Error updating cart item: {}", itemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update cart item");
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId) {
        log.info("Removing item from cart: {}", itemId);
        try {
            cartService.removeFromCart(getCurrentUserId(), (long) Math.toIntExact(itemId));
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Cart item not found: {}", itemId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cart item not found");
        } catch (Exception e) {
            log.error("Error removing item from cart: {}", itemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove item from cart");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart() {
        log.info("Clearing cart for user: {}", getCurrentUserId());
        try {
            cartService.clearCart(getCurrentUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing cart for user: {}", getCurrentUserId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to clear cart");
        }
    }

    // TODO: Implement proper user authentication
    private Long getCurrentUserId() {
        // This should be replaced with actual user authentication logic
        return (long) Math.toIntExact(3L);
    }
}