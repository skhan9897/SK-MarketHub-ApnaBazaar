package com.skmarkethub.backend.kafka;

import com.skmarkethub.backend.service.EmailProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);
    private final EmailProviderService emailProviderService;

    public EmailNotificationConsumer(EmailProviderService emailProviderService) {
        this.emailProviderService = emailProviderService;
    }

    @KafkaListener(topics = "email-otp-requested", groupId = "skmarkethub-email-group")
    public void handleOtpRequestedEvent(Map<String, Object> payload) {
        log.info("Received Kafka event 'email-otp-requested': {}", payload);
        String email = (String) payload.get("email");
        String otpCode = (String) payload.get("otpCode");

        if (email != null && otpCode != null) {
            emailProviderService.sendOtpHtmlEmail(email, otpCode);
        }
    }

    @KafkaListener(topics = "user-registered", groupId = "skmarkethub-email-group")
    public void handleUserRegisteredEvent(Map<String, Object> payload) {
        log.info("Received Kafka event 'user-registered': {}", payload);
    }
}
