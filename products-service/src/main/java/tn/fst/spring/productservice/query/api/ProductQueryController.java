package tn.fst.spring.productservice.query.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.productservice.query.models.ProductReadModel;
import tn.fst.spring.productservice.query.repository.ProductRepository;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductRepository productRepository;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductReadModel> getProduct(@PathVariable String productId) {
        return productRepository.findById(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<ProductReadModel> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/available")
    public List<ProductReadModel> getAvailableProducts(@RequestParam(defaultValue = "0") int minStock) {
        return productRepository.findByAvailableStockGreaterThan(minStock);
    }
}