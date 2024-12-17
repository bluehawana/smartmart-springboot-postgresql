package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.model.CartItem;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public String createCheckoutSession(List<CartItem> items) throws StripeException {
        SessionCreateParams params;
        try {
            Stripe.apiKey = stripeSecretKey;
            log.info("Creating checkout session with items: {}", items);

            List<SessionCreateParams.LineItem> lineItems = items.stream()
                    .map(item -> SessionCreateParams.LineItem.builder()
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("sek")
                                    .setUnitAmount(item.getPrice().multiply(new BigDecimal("100")).longValue())
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName(item.getName())
                                            .build())
                                    .build())
                            .setQuantity(Long.valueOf(item.getQuantity()))
                            .build())
                    .collect(Collectors.toList());

            params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:3000/success?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:3000/cart")
                    .addAllLineItem(lineItems)
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setShippingAddressCollection(
                            SessionCreateParams.ShippingAddressCollection.builder()
                                    .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.SE)
                                    .build()
                    )
                    .setCustomerCreation(SessionCreateParams.CustomerCreation.ALWAYS)
                    // 明确要求收集邮箱
                    .setCustomerEmail(null)  // 这会强制 Stripe 收集邮箱
                    .build();
        } catch (Exception e) {
            log.error("Failed to create checkout session", e);
            throw new RuntimeException("Failed to create checkout session: " + e.getMessage());
        }

        Session session = Session.create(params);
        return session.getUrl();
    }
}
