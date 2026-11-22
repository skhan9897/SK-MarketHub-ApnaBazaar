package com.skmarkethub.backend.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EmailNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EmailNotificationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOtpRequestedEvent(String email, String otpCode) {
        log.info("Publishing event 'email-otp-requested' for email: {}", email);
        try {
            kafkaTemplate.send("email-otp-requested", email, Map.of(
                    "email", email,
                    "otpCode", otpCode,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.warn("Kafka event publishing failed, executing direct fallback: {}", e.getMessage());
        }
    }

    public void sendUserRegisteredEvent(String email, String firstName) {
        log.info("Publishing event 'user-registered' for user: {}", email);
        try {
            kafkaTemplate.send("user-registered", email, Map.of(
                    "email", email,
                    "firstName", firstName,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.warn("Kafka event publishing failed: {}", e.getMessage());
        }
    }
}
