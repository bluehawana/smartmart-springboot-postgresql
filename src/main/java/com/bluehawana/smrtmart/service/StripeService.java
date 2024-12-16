package com.bluehawana.smrtmart.service;

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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final CartItemService cartItemService;

    public String createCheckoutSession() throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        log.info("Creating Stripe checkout session with API key: {}", stripeSecretKey.substring(0, 8) + "...");

        List<CartItemDTO> cartItems = cartItemService.getAllItems();
        log.info("Creating checkout session for {} items", cartItems.size());

        List<SessionCreateParams.LineItem> lineItems = cartItems.stream()
                .map(item -> {
                    long unitAmount = item.getPrice().multiply(new BigDecimal("100")).longValue();
                    log.debug("Adding item: {} with price: {}", item.getName(), unitAmount);
                    return SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("sek")
                                    .setUnitAmount(unitAmount)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getName())
                                            .build())
                                    .build())
                            .setQuantity(Long.valueOf(item.getQuantity()))
                            .build();
                })
                .collect(Collectors.toList());

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setLocale(SessionCreateParams.Locale.SV)
                .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:3000/cart")
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);
        log.info("Checkout session created with ID: {}", session.getId());

        return session.getUrl();
    }
}