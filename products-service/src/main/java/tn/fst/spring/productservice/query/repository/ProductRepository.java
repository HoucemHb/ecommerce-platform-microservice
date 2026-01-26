package tn.fst.spring.productservice.query.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.fst.spring.productservice.query.models.ProductReadModel;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductReadModel, String> {
    List<ProductReadModel> findByAvailableStockGreaterThan(int minStock);
}