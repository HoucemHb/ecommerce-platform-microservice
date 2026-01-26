package tn.fst.spring.notificationsservice.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.notificationsservice.query.models.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByOrderId(String orderId);
    List<Notification> findByCustomerId(String customerId);
    List<Notification> findByStatus(Notification.NotificationStatus status);
}