package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.service.CartItemService;
import com.bluehawana.smrtmart.service.StripeService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")  // 确保路径正确
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CheckoutController {
    private final StripeService stripeService;
    private final CartItemService cartItemService;

    @PostMapping("")  // 处理 POST /api/checkout
    public ResponseEntity<?> createCheckoutSession() {
        try {
            log.info("Creating checkout session...");

            List<CartItemDTO> cartItems = cartItemService.getAllItems();
            if (cartItems.isEmpty()) {
                log.warn("Cart is empty");
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Cart is empty",
                        "success", false
                ));
            }

            String sessionUrl = stripeService.createCheckoutSession();
            log.info("Checkout session created successfully");

            return ResponseEntity.ok(Map.of(
                    "url", sessionUrl,
                    "success", true
            ));

        } catch (StripeException e) {
            log.error("Stripe error: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Payment processing error: " + e.getMessage(),
                    "success", false
            ));
        } catch (Exception e) {
            log.error("Error creating checkout session: ", e);
            return ResponseEntity.status(500).body(Map.of(
                    "message", "Failed to create checkout session",
                    "success", false
            ));
        }
    }
}