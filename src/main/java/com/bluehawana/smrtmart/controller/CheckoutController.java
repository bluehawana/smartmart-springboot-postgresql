package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CheckoutRequest;
import com.bluehawana.smrtmart.service.EmailService;
import com.bluehawana.smrtmart.service.StripeService;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@Slf4j
public class CheckoutController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutRequest request) {
        try {
            String checkoutUrl = stripeService.createCheckoutSession(request.getItems());
            return ResponseEntity.ok(Map.of("url", checkoutUrl));
        } catch (Exception e) {
            log.error("Checkout error:", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/success")
    public ResponseEntity<?> confirmOrder(@RequestParam String session_id) {
        try {
            log.info("Processing success for session: {}", session_id);

            Session session = Session.retrieve(session_id);
            String customerEmail = null;

            // 尝试多种方式获取邮箱
            if (session.getCustomerDetails() != null) {
                customerEmail = session.getCustomerDetails().getEmail();
                log.info("Found email from customer details: {}", customerEmail);
            }

            if ((customerEmail == null || customerEmail.isEmpty()) && session.getCustomer() != null) {
                try {
                    Customer customer = Customer.retrieve(session.getCustomer());
                    customerEmail = customer.getEmail();
                    log.info("Found email from customer: {}", customerEmail);
                } catch (Exception e) {
                    log.warn("Could not retrieve customer email: {}", e.getMessage());
                }
            }

            if (customerEmail == null || customerEmail.isEmpty()) {
                log.error("No email found in session {}", session_id);
                return ResponseEntity.status(400)
                        .body(Map.of(
                                "success", false,
                                "message", "Customer email is required"
                        ));
            }

            emailService.sendOrderConfirmation(
                    customerEmail,
                    session_id,
                    session.getAmountTotal() / 100.0
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Order confirmed and email sent",
                    "email", customerEmail
            ));

        } catch (Exception e) {
            log.error("Error processing success: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to confirm order: " + e.getMessage()
                    ));
        }
    }
}