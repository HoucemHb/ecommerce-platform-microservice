package tn.fst.spring.notificationsservice.query.models;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String notificationId;
    private String orderId;
    private String customerId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(length = 2000)
    private String message;

    private String recipient;

    private Instant createdAt;
    private Instant sentAt;

    public enum NotificationType {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        PAYMENT_VALIDATED,
        PAYMENT_FAILED,
        STOCK_RESERVED,
        STOCK_RESERVATION_FAILED,
        STOCK_RELEASED
    }

    public enum NotificationChannel {
        EMAIL,
        SMS,
        PUSH,
        LOG
    }

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }
}
