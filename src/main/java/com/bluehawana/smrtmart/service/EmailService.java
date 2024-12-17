package com.bluehawana.smrtmart.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.api.secret}")
    private String apiSecret;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    public void sendOrderConfirmation(String toEmail, String orderId, double total) {
        try {
            MailjetClient client = new MailjetClient(apiKey, apiSecret);

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", "SmartMart"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", toEmail)))
                                    .put(Emailv31.Message.SUBJECT, "Order Confirmation - SmartMart")
                                    .put(Emailv31.Message.TEXTPART, String.format("""
                            Dear Customer,
                            
                            Thank you for shopping at SmartMart! Your order has been confirmed.
                            
                            Order Details:
                            Order ID: %s
                            Total Amount: €%.2f
                            
                            We will process your order shortly and send you a shipping confirmation.
                            
                            If you have any questions about your order, please contact our customer service.
                            
                            Best regards,
                            SmartMart Team
                            """, orderId, total))));

            MailjetResponse response = client.post(request);
            if (response.getStatus() != 200) {
                log.error("Failed to send email. Status: {}, Data: {}",
                        response.getStatus(), response.getData());
                throw new RuntimeException("Failed to send email");
            }

            log.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }
}