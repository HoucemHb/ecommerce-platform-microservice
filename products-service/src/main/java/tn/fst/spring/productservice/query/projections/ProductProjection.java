package tn.fst.spring.productservice.query.projections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.fst.spring.productservice.query.models.ProductReadModel;
import tn.fst.spring.productservice.query.repository.ProductRepository;
import tn.fst.spring.sharedkernel.events.*;

@Component
@RequiredArgsConstructor
@Slf4j
@ProcessingGroup("product-projections")  // ← THIS WAS MISSING!
public class ProductProjection {

    private final ProductRepository productRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        log.info("=== PROJECTING ProductCreatedEvent ===");
        log.info("ProductId: {}", event.getProductId());
        log.info("Name: {}", event.getName());
        log.info("Initial Stock: {}", event.getInitialStock());

        ProductReadModel product = new ProductReadModel();
        product.setProductId(event.getProductId());
        product.setName(event.getName());
        product.setDescription(event.getDescription());
        product.setPrice(event.getPrice().getAmount());
        product.setCurrency(event.getPrice().getCurrency());
        product.setAvailableStock(event.getInitialStock());

        productRepository.save(product);

        log.info("✅ Product saved to query database: {}", event.getProductId());

        // Verify it was saved
        productRepository.findById(event.getProductId()).ifPresentOrElse(
                p -> log.info("✅ Verified: Product {} exists in database with stock: {}",
                        p.getProductId(), p.getAvailableStock()),
                () -> log.error("❌ ERROR: Product {} NOT found in database after save!",
                        event.getProductId())
        );
    }

    @EventHandler
    public void on(StockReservedEvent event) {
        log.info("=== PROJECTING StockReservedEvent ===");
        log.info("ProductId: {}", event.getProductId());
        log.info("OrderId: {}", event.getOrderId());
        log.info("Quantity: {}", event.getQuantity());
        log.info("Remaining Stock: {}", event.getRemainingStock());

        productRepository.findById(event.getProductId()).ifPresentOrElse(
                product -> {
                    product.setAvailableStock(event.getRemainingStock());
                    product.getReservations().put(event.getOrderId(), event.getQuantity());
                    productRepository.save(product);
                    log.info("✅ Stock updated for product: {}", event.getProductId());
                },
                () -> log.error("❌ Product not found: {}", event.getProductId())
        );
    }

    @EventHandler
    public void on(StockReleasedEvent event) {
        log.info("=== PROJECTING StockReleasedEvent ===");
        log.info("ProductId: {}", event.getProductId());
        log.info("OrderId: {}", event.getOrderId());

        productRepository.findById(event.getProductId()).ifPresentOrElse(
                product -> {
                    Integer releasedQuantity = product.getReservations().remove(event.getOrderId());
                    if (releasedQuantity != null) {
                        product.setAvailableStock(product.getAvailableStock() + releasedQuantity);
                        productRepository.save(product);
                        log.info("✅ Stock released: {} units returned to product: {}",
                                releasedQuantity, event.getProductId());
                    } else {
                        log.warn("⚠️ No reservation found for order: {}", event.getOrderId());
                    }
                },
                () -> log.error("❌ Product not found: {}", event.getProductId())
        );
    }

    @EventHandler
    public void on(StockUpdatedEvent event) {
        log.info("=== PROJECTING StockUpdatedEvent ===");
        log.info("ProductId: {}", event.getProductId());
        log.info("Old Stock: {}", event.getOldStock());
        log.info("New Stock: {}", event.getNewStock());

        productRepository.findById(event.getProductId()).ifPresentOrElse(
                product -> {
                    product.setAvailableStock(event.getNewStock());
                    productRepository.save(product);
                    log.info("✅ Stock updated for product: {}", event.getProductId());
                },
                () -> log.error("❌ Product not found: {}", event.getProductId())
        );
    }

    @ResetHandler
    public void reset() {
        log.warn("⚠️ RESETTING Product projection - deleting all read models");
        productRepository.deleteAll();
        log.info("✅ All product read models deleted");
    }
    @EventHandler
    public void on(DeleteProductEvent event, @Autowired ProductRepository repository) {
        repository.deleteById(event.getProductId());
    }

}