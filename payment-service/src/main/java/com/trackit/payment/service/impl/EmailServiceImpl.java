package com.trackit.payment.service.impl;

import com.trackit.payment.entity.Payment;
import com.trackit.payment.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendPaymentConfirmationEmail(Payment payment) {
        log.info("========================================");
        log.info("📧 EMAIL SERVICE - SENDING EMAIL");
        log.info("   To: {}", payment.getEmail());
        log.info("   Order: {}", payment.getOrderId());
        log.info("   Amount: ₹{}", payment.getAmount());
        log.info("========================================");

        if (payment.getEmail() == null || payment.getEmail().isEmpty()) {
            log.error("❌ Cannot send email - no email address!");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(payment.getEmail());
            helper.setSubject("✅ Payment Confirmed - Order " + payment.getOrderId());
            helper.setText(buildEmailHtml(payment), true);

            mailSender.send(message);
            
            log.info("✅ EMAIL SENT SUCCESSFULLY to: {}", payment.getEmail());

        } catch (Exception e) {
            log.error("❌ EMAIL FAILED: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void sendPaymentFailedEmail(Payment payment, String reason) {
        log.info("📧 Sending payment failed email to: {}", payment.getEmail());
      
    }

    private String buildEmailHtml(Payment payment) {
        double total = payment.getAmount() != null ? payment.getAmount() : 0;
        double subtotal = total / 1.18;
        double tax = total - subtotal;

        String paymentTime = payment.getPaymentTime() != null
                ? payment.getPaymentTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
                : "N/A";

        String customerName = payment.getEmail() != null
                ? payment.getEmail().split("@")[0]
                : "Customer";

        return String.format("""
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5;">
                <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    
                    <div style="background: #1e293b; color: white; padding: 30px; text-align: center;">
                        <h1 style="margin: 0; font-size: 28px;">TRACKIT</h1>
                        <p style="margin: 10px 0 0 0; color: #94a3b8;">Payment Confirmation</p>
                    </div>
                    
                    <div style="background: #22c55e; color: white; padding: 15px; text-align: center; font-weight: bold;">
                        ✓ PAYMENT SUCCESSFUL
                    </div>
                    
                    <div style="padding: 30px;">
                        <h2 style="margin: 0 0 20px 0;">Hello, %s!</h2>
                        <p style="color: #666; line-height: 1.6;">Your payment has been processed successfully. Here are your transaction details:</p>
                        
                        <div style="background: #fef3c7; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0;">
                            <p style="margin: 0; color: #92400e; font-size: 12px; text-transform: uppercase;">Amount Paid</p>
                            <p style="margin: 5px 0 0 0; font-size: 32px; font-weight: bold; color: #1e293b;">₹%.2f</p>
                        </div>
                        
                        <div style="background: #f8fafc; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                            <table style="width: 100%%; border-collapse: collapse;">
                                <tr>
                                    <td style="padding: 8px 0; color: #666;">Subtotal</td>
                                    <td style="padding: 8px 0; text-align: right; font-weight: 500;">₹%.2f</td>
                                </tr>
                                <tr>
                                    <td style="padding: 8px 0; color: #666;">GST (18%%)</td>
                                    <td style="padding: 8px 0; text-align: right; font-weight: 500;">₹%.2f</td>
                                </tr>
                                <tr style="border-top: 2px solid #e2e8f0;">
                                    <td style="padding: 12px 0 8px 0; font-weight: bold; color: #1e293b;">Total</td>
                                    <td style="padding: 12px 0 8px 0; text-align: right; font-weight: bold; color: #1e293b; font-size: 18px;">₹%.2f</td>
                                </tr>
                            </table>
                        </div>
                        
                        <table style="width: 100%%; border-collapse: collapse; margin-bottom: 25px;">
                            <tr>
                                <td style="padding: 12px 0; color: #666; border-bottom: 1px solid #eee;">Order ID</td>
                                <td style="padding: 12px 0; text-align: right; border-bottom: 1px solid #eee; font-family: monospace; font-weight: 600;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 0; color: #666; border-bottom: 1px solid #eee;">Payment ID</td>
                                <td style="padding: 12px 0; text-align: right; border-bottom: 1px solid #eee; font-family: monospace; font-weight: 600;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 0; color: #666; border-bottom: 1px solid #eee;">Transaction ID</td>
                                <td style="padding: 12px 0; text-align: right; border-bottom: 1px solid #eee; font-family: monospace; font-size: 12px;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 0; color: #666;">Date & Time</td>
                                <td style="padding: 12px 0; text-align: right; font-weight: 500;">%s</td>
                            </tr>
                        </table>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="https://trackit.com/order/%s" style="display: inline-block; background: #1e293b; color: #facc15; text-decoration: none; padding: 15px 35px; border-radius: 8px; font-weight: bold; font-size: 14px;">
                                TRACK YOUR ORDER →
                            </a>
                        </div>
                        
                        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; padding: 15px; text-align: center;">
                            <p style="margin: 0; color: #166534; font-size: 14px;">
                                📦 Your shipment will be processed shortly. You'll receive tracking updates via email.
                            </p>
                        </div>
                    </div>
                    
                    <div style="background: #1e293b; padding: 25px; text-align: center; color: #94a3b8;">
                        <p style="margin: 0; font-size: 14px;">Need help? Contact us at</p>
                        <p style="margin: 8px 0 0 0; color: white; font-weight: 600;">support@trackit.com | +91 98765 43210</p>
                        <p style="margin: 15px 0 0 0; font-size: 12px; color: #64748b;">© 2026 TrackIt Logistics. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                customerName,
                total,
                subtotal,
                tax,
                total,
                payment.getOrderId() != null ? payment.getOrderId() : "N/A",
                payment.getPaymentId() != null ? payment.getPaymentId() : "N/A",
                payment.getRazorpayPaymentId() != null ? payment.getRazorpayPaymentId() : "N/A",
                paymentTime,
                payment.getOrderId() != null ? payment.getOrderId() : ""
        );
    }
}