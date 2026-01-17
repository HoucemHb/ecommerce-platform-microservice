package tn.fst.spring.ordersservice.query.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.ordersservice.query.models.OrderReadModel;
import tn.fst.spring.ordersservice.query.repository.OrderRepository;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderRepository orderRepository;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderReadModel> getOrder(@PathVariable String orderId) {
        return orderRepository.findById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<OrderReadModel> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderReadModel> getOrdersByCustomer(@PathVariable String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @GetMapping("/status/{status}")
    public List<OrderReadModel> getOrdersByStatus(@PathVariable OrderReadModel.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
}
