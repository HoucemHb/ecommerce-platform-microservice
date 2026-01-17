package tn.fst.spring.ordersservice.query.repository;

import com.ecommerce.orders.query.models.OrderReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderReadModel, String> {
    List<OrderReadModel> findByCustomerId(String customerId);
    List<OrderReadModel> findByStatus(OrderReadModel.OrderStatus status);
}
