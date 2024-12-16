package com.bluehawana.smrtmart.controller;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.service.CartItemService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class CheckoutController {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${stripe.success.url}")
    private String successUrl = "http://localhost:3000/success";

    @Value("${stripe.cancel.url}")
    private String cancelUrl = "http://localhost:3000/cart";

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<?> createCheckoutSession() {
        try {
            // 初始化 Stripe API
            Stripe.apiKey = stripeSecretKey;

            // 获取购物车商品
            List<CartItemDTO> cartItems = cartItemService.getAllItems();

            // 验证购物车不为空
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Cart is empty", "Please add items to your cart before checkout"));
            }

            // 创建 Stripe session
            Session session = createStripeSession(cartItems);

            // 返回 session URL
            Map<String, String> response = new HashMap<>();
            response.put("url", session.getUrl());
            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            log.error("Stripe error during checkout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Payment Error", "Unable to process payment at this time"));
        } catch (Exception e) {
            log.error("Server error during checkout: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(createErrorResponse(
                    "Server Error", "An unexpected error occurred"));
        }
    }

    private Session createStripeSession(List<CartItemDTO> cartItems) throws StripeException {
        // 创建商品行项目
        List<SessionCreateParams.LineItem> lineItems = cartItems.stream()
                .map(this::createLineItem)
                .toList();

        // 构建 session 参数
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .setShippingAddressCollection(
                        SessionCreateParams.ShippingAddressCollection.builder()
                                .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US)
                                .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.CA)
                                .build()
                )
                .setPhoneNumberCollection(
                        SessionCreateParams.PhoneNumberCollection.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return Session.create(params);
    }

    private SessionCreateParams.LineItem createLineItem(CartItemDTO item) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(item.getPrice().multiply(new java.math.BigDecimal("100")).longValue())
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(item.getName())
                                                .setDescription(item.getName()) // 可以添加更详细的描述
                                                .build()
                                )
                                .build()
                )
                .setQuantity(Long.valueOf(item.getQuantity()))
                .build();
    }

    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        return response;
    }

    // 处理支付成功回调
    @GetMapping("/success")
    public ResponseEntity<?> handlePaymentSuccess(@RequestParam("session_id") String sessionId) {
        try {
            Stripe.apiKey = stripeSecretKey;
            Session session = Session.retrieve(sessionId);

            // 验证支付状态
            if ("complete".equals(session.getPaymentStatus())) {
                // 清空购物车
                cartItemService.getAllItems()
                        .forEach(item -> cartItemService.removeItem(item.getId()));

                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Payment processed successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Payment Incomplete", "Payment has not been completed"));
            }
        } catch (StripeException e) {
            log.error("Error verifying payment status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(
                    "Verification Error", "Unable to verify payment status"));
        }
    }
}