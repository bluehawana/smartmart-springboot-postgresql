package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.service.EmailService;
import com.stripe.model.checkout.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class OrderController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/order/checkout/success")
    public ResponseEntity<?> confirmOrder(@RequestParam String session_id) {
        try {
            log.info("Processing success for session: {}", session_id);

            Session session = Session.retrieve(session_id);
            String customerEmail = session.getCustomerEmail();

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