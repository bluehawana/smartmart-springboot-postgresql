package com.bluehawana.smrtmart.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmation(String toEmail, String orderId, double total) {
        try {
            log.info("Preparing to send order confirmation email to: {}", toEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Order Confirmation - SmartMart");

            String content = String.format("""
                Dear Customer,
                
                Thank you for your purchase! Your order has been confirmed.
                
                Order Details:
                Order ID: %s
                Total Amount: €%.2f
                
                We will process your order shortly.
                
                Best regards,
                SmartMart Team
                """, orderId, total);

            helper.setText(content, false);
            mailSender.send(message);

            log.info("Order confirmation email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send order confirmation email", e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}