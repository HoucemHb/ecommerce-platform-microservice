package tn.fst.spring.productservice.query.projections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import tn.fst.spring.productservice.query.models.ProductReadModel;
import tn.fst.spring.productservice.query.repository.ProductRepository;
import tn.fst.spring.sharedkernel.events.ProductCreatedEvent;
import tn.fst.spring.sharedkernel.events.StockReleasedEvent;
import tn.fst.spring.sharedkernel.events.StockReservedEvent;
import tn.fst.spring.sharedkernel.events.StockUpdatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductProjection {

    private final ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        log.info("Projecting ProductCreatedEvent for productId: {}", event.getProductId());

        ProductReadModel product = new ProductReadModel();
        product.setProductId(event.getProductId());
        product.setName(event.getName());
        product.setDescription(event.getDescription());
        product.setPrice(event.getPrice().getAmount());
        product.setCurrency(event.getPrice().getCurrency());
        product.setAvailableStock(event.getInitialStock());

        productRepository.save(product);
    }

    @EventHandler
    public void on(StockReservedEvent event) {
        log.info("Projecting StockReservedEvent for productId: {}", event.getProductId());

        productRepository.findById(event.getProductId()).ifPresent(product -> {
            product.setAvailableStock(event.getRemainingStock());
            product.getReservations().put(event.getOrderId(), event.getQuantity());
            productRepository.save(product);
        });
    }

    @EventHandler
    public void on(StockReleasedEvent event) {
        log.info("Projecting StockReleasedEvent for productId: {}", event.getProductId());

        productRepository.findById(event.getProductId()).ifPresent(product -> {
            Integer releasedQuantity = product.getReservations().remove(event.getOrderId());
            if (releasedQuantity != null) {
                product.setAvailableStock(product.getAvailableStock() + releasedQuantity);
                productRepository.save(product);
            }
        });
    }

    @EventHandler
    public void on(StockUpdatedEvent event) {
        log.info("Projecting StockUpdatedEvent for productId: {}", event.getProductId());

        productRepository.findById(event.getProductId()).ifPresent(product -> {
            product.setAvailableStock(event.getNewStock());
            productRepository.save(product);
        });
    }

    @ResetHandler
    public void reset() {
        log.warn("Resetting Product projection - deleting all read models");
        productRepository.deleteAll();
    }
}