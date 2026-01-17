package tn.fst.spring.ordersservice.query.projections;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.ordersservice.query.models.OrderLineReadModel;
import tn.fst.spring.ordersservice.query.models.OrderReadModel;
import tn.fst.spring.ordersservice.query.repository.OrderRepository;
import tn.fst.spring.sharedkernel.events.OrderCancelledEvent;
import tn.fst.spring.sharedkernel.events.OrderConfirmedEvent;
import tn.fst.spring.sharedkernel.events.OrderCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProjection {

    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Projecting OrderCreatedEvent for orderId: {}", event.getOrderId());

        OrderReadModel order = new OrderReadModel();
        order.setOrderId(event.getOrderId());
        order.setCustomerId(event.getCustomerId());
        order.setStatus(OrderReadModel.OrderStatus.PENDING);
        order.setTotalAmount(event.getTotalAmount().getAmount());
        order.setCurrency(event.getTotalAmount().getCurrency());
        order.setShippingStreet(event.getShippingAddress().getStreet());
        order.setShippingCity(event.getShippingAddress().getCity());
        order.setShippingZipCode(event.getShippingAddress().getZipCode());
        order.setShippingCountry(event.getShippingAddress().getCountry());
        order.setCreatedAt(event.getCreatedAt());

        event.getItems().forEach(item -> {
            OrderLineReadModel line = new OrderLineReadModel();
            line.setOrder(order);
            line.setProductId(item.getProductId());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice().getAmount());
            line.setCurrency(item.getUnitPrice().getCurrency());
            order.getItems().add(line);
        });

        orderRepository.save(order);
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        log.info("Projecting OrderConfirmedEvent for orderId: {}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderReadModel.OrderStatus.CONFIRMED);
            order.setConfirmedAt(event.getConfirmedAt());
            orderRepository.save(order);
        });
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Projecting OrderCancelledEvent for orderId: {}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderReadModel.OrderStatus.CANCELLED);
            order.setCancelledAt(event.getCancelledAt());
            orderRepository.save(order);
        });
    }

    @ResetHandler
    public void reset() {
        log.warn("Resetting Order projection - deleting all read models");
        orderRepository.deleteAll();
    }
}
