package tn.fst.spring.notificationsservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(String recipient, String subject, String body) {
        // In production, integrate with SendGrid, AWS SES, etc.
        log.info("=====================================================");
        log.info("SENDING EMAIL");
        log.info("To: {}", recipient);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
        log.info("=====================================================");

        // Simulate email sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        log.info("=====================================================");
        log.info("SENDING SMS");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("=====================================================");
    }
}
