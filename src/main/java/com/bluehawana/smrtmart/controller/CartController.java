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
            return ResponseEntity.ok(cartService.getCurrentCart());
        } catch (Exception e) {
            log.error("Error getting cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@RequestBody CartItemDTO cartItem) {
        log.info("Adding item to cart: {}", cartItem);
        try {
            return ResponseEntity.ok(cartService.addToCart(cartItem));
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
            return ResponseEntity.ok(cartService.updateCartItem(itemId, update));
        } catch (ResourceNotFoundException e) {
            log.error("Cart item not found", e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer id) {
        log.info("Removing item from cart: {}", id);
        try {
            cartService.removeFromCart(id);
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
            cartService.clearCart();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error clearing cart", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}