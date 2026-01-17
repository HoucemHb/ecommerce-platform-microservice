package tn.fst.spring.ordersservice.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.ordersservice.query.models.OrderReadModel;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderReadModel, String> {
    List<OrderReadModel> findByCustomerId(String customerId);
    List<OrderReadModel> findByStatus(OrderReadModel.OrderStatus status);
}
