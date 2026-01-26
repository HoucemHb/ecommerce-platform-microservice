package tn.fst.spring.notificationsservice.query.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.notificationsservice.query.models.Notification;
import tn.fst.spring.notificationsservice.query.repository.NotificationRepository;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationQueryController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public List<Notification> getNotificationsByOrder(@PathVariable String orderId) {
        return notificationRepository.findByOrderId(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public List<Notification> getNotificationsByCustomer(@PathVariable String customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    @GetMapping
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/status/{status}")
    public List<Notification> getNotificationsByStatus(@PathVariable Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }
}