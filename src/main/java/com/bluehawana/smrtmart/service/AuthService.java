// EmailService.java
package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.LoginRequest;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Value("${mailjet.api.key}")
    private String apiKey;

    @Value("${mailjet.api.secret}")
    private String apiSecret;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    public void sendLoginOTP(String recipientEmail, String otp) {
        MailjetClient client = new MailjetClient(apiKey, apiSecret, new ClientOptions("v3.1"));
        MailjetRequest request;
        MailjetResponse response;

        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", senderEmail)
                                        .put("Name", "Your Eshop"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", recipientEmail)))
                                .put(Emailv31.Message.SUBJECT, "Your One-Time Login Token")
                                .put(Emailv31.Message.TEXTPART, "Your login token is: " + otp)
                                .put(Emailv31.Message.HTMLPART, "<h3>Your login token is: " + otp + "</h3>")));

        try {
            response = client.post(request);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to send email: " + response.getStatus() + " " + response.getData());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public Object login(LoginRequest loginRequest) {
        return "Login successful";
    }

    public Object verifyToken(String token) {
        return "Token verified";
    }
}