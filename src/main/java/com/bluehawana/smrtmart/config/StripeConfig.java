package com.bluehawana.smrtmart.config;

import com.bluehawana.smrtmart.dto.CartItemDTO;
import com.bluehawana.smrtmart.service.CartItemService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeConfig {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.success.url:http://localhost:3000/success}")
    private String successUrl;

    @Value("${stripe.cancel.url:http://localhost:3000/cart}")
    private String cancelUrl;

    private final CartItemService cartItemService;

    public String createCheckoutSession() throws StripeException {
        log.info("Creating Stripe checkout session");
        Stripe.apiKey = stripeApiKey;

        // 获取购物车中的所有商品
        List<CartItemDTO> cartItems = cartItemService.getAllItems();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        List<SessionCreateParams.LineItem> lineItems = cartItems.stream()
                .map(item -> SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(item.getPrice().multiply(new java.math.BigDecimal("100")).longValue())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(item.getName())
                                        .build())
                                .build())
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .build())
                .toList();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                // 添加配送地址收集
                .setShippingAddressCollection(
                        SessionCreateParams.ShippingAddressCollection.builder()
                                .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.US)
                                .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.CA)
                                .build()
                )
                // 添加电话号码收集
                .setPhoneNumberCollection(
                        SessionCreateParams.PhoneNumberCollection.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        Session session = Session.create(params);
        log.info("Stripe checkout session created: {}", session.getId());
        return session.getUrl();
    }

    public Session retrieveSession(String sessionId) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        return Session.retrieve(sessionId);
    }
}