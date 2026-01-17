package tn.fst.spring.notificationsservice.eventhandlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.notificationsservice.query.models.Notification;
import tn.fst.spring.notificationsservice.query.repository.NotificationRepository;
import tn.fst.spring.notificationsservice.service.EmailService;
import tn.fst.spring.sharedkernel.events.*;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventHandler {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Your order %s has been created successfully. Total amount: %s %s. We'll notify you once it's confirmed.",
                event.getOrderId(),
                event.getTotalAmount().getAmount(),
                event.getTotalAmount().getCurrency()
        );

        sendNotification(
                event.getOrderId(),
                event.getCustomerId(),
                Notification.NotificationType.ORDER_CREATED,
                message
        );
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        log.info("Handling OrderConfirmedEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Great news! Your order %s has been confirmed and is being processed. " +
                        "You'll receive shipping updates soon.",
                event.getOrderId()
        );

        sendNotification(
                event.getOrderId(),
                "customer-" + event.getOrderId(), // In real scenario, get from order details
                Notification.NotificationType.ORDER_CONFIRMED,
                message
        );
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Handling OrderCancelledEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Your order %s has been cancelled. Reason: %s. " +
                        "If payment was processed, refund will be initiated.",
                event.getOrderId(),
                event.getReason()
        );

        sendNotification(
                event.getOrderId(),
                "customer-" + event.getOrderId(),
                Notification.NotificationType.ORDER_CANCELLED,
                message
        );
    }

    @EventHandler
    public void on(PaymentValidatedEvent event) {
        log.info("Handling PaymentValidatedEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Payment of %s %s has been successfully processed for your order %s using %s.",
                event.getAmount().getAmount(),
                event.getAmount().getCurrency(),
                event.getOrderId(),
                event.getPaymentMethod()
        );

        sendNotification(
                event.getOrderId(),
                event.getCustomerId(),
                Notification.NotificationType.PAYMENT_VALIDATED,
                message
        );
    }

    @EventHandler
    public void on(PaymentFailedEvent event) {
        log.info("Handling PaymentFailedEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Payment processing failed for order %s. Reason: %s. " +
                        "Please update your payment method and try again.",
                event.getOrderId(),
                event.getReason()
        );

        sendNotification(
                event.getOrderId(),
                "customer-" + event.getOrderId(),
                Notification.NotificationType.PAYMENT_FAILED,
                message
        );
    }

    @EventHandler
    public void on(StockReservedEvent event) {
        log.info("Handling StockReservedEvent for notification - orderId: {}", event.getOrderId());

        // Internal notification - could be sent to warehouse/inventory team
        String message = String.format(
                "Stock reserved: Product %s, Quantity: %d for Order %s. Remaining stock: %d",
                event.getProductId(),
                event.getQuantity(),
                event.getOrderId(),
                event.getRemainingStock()
        );

        Notification notification = createNotification(
                event.getOrderId(),
                "inventory-team",
                Notification.NotificationType.STOCK_RESERVED,
                message
        );

        log.info("Stock reservation notification: {}", message);
        notificationRepository.save(notification);
    }

    @EventHandler
    public void on(StockReservationFailedEvent event) {
        log.info("Handling StockReservationFailedEvent for notification - orderId: {}", event.getOrderId());

        String message = String.format(
                "Unable to reserve stock for order %s. Product %s - Requested: %d, Available: %d",
                event.getOrderId(),
                event.getProductId(),
                event.getRequestedQuantity(),
                event.getAvailableStock()
        );

        sendNotification(
                event.getOrderId(),
                "customer-" + event.getOrderId(),
                Notification.NotificationType.STOCK_RESERVATION_FAILED,
                message
        );
    }

    private void sendNotification(String orderId, String customerId,
                                  Notification.NotificationType type, String message) {
        Notification notification = createNotification(orderId, customerId, type, message);

        // Send email
        String recipient = customerId + "@example.com"; // In real scenario, fetch from customer service
        emailService.sendEmail(recipient, type.toString(), message);

        notification.setStatus(Notification.NotificationStatus.SENT);
        notification.setSentAt(Instant.now());

        notificationRepository.save(notification);

        log.info("Notification sent successfully for order: {}", orderId);
    }

    private Notification createNotification(String orderId, String customerId,
                                            Notification.NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setOrderId(orderId);
        notification.setCustomerId(customerId);
        notification.setType(type);
        notification.setChannel(Notification.NotificationChannel.EMAIL);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notification.setMessage(message);
        notification.setRecipient(customerId + "@example.com");
        notification.setCreatedAt(Instant.now());

        return notification;
    }
}