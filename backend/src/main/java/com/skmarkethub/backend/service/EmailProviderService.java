package com.skmarkethub.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailProviderService {

    private static final Logger log = LoggerFactory.getLogger(EmailProviderService.class);
    private final JavaMailSender mailSender;

    public EmailProviderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpHtmlEmail(String toEmail, String otpCode) {
        log.info("Sending Email OTP to: {}", toEmail);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("SK-MarketHub Verification Code: " + otpCode);

            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>SK-MarketHub OTP</title>
                </head>
                <body style="margin: 0; padding: 0; background-color: #080B14; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">
                    <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background-color: #080B14; padding: 40px 20px;">
                        <tr>
                            <td align="center">
                                <table width="500" border="0" cellspacing="0" cellpadding="0" style="background-color: #101B35; border: 2px solid #D4AF37; border-radius: 12px; padding: 32px; text-align: center; color: #FFFFFF;">
                                    <tr>
                                        <td>
                                            <h1 style="color: #E7B85C; font-size: 26px; margin: 0 0 6px 0; font-weight: 800;">SK-MarketHub</h1>
                                            <p style="color: #F5D994; font-size: 12px; margin: 0 0 24px 0; font-weight: 700; letter-spacing: 1px;">SHOP SMART. LIVE BETTER.</p>
                                            
                                            <p style="color: #B9C2D1; font-size: 14px; margin-bottom: 20px;">Use the following verification code to complete your registration:</p>
                                            
                                            <div style="background-color: #080B14; border: 1px dashed #D4AF37; padding: 16px; border-radius: 8px; font-size: 32px; font-weight: 800; color: #E7B85C; letter-spacing: 6px; margin-bottom: 24px;">
                                                """ + otpCode + """
                                            </div>
                                            
                                            <p style="color: #94a3b8; font-size: 12px; margin: 0;">This code is valid for <strong>5 minutes</strong>. Do not share this OTP with anyone.</p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
            """;

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email OTP successfully delivered to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send Email OTP to {}: {}", toEmail, e.getMessage());
        }
    }
}
