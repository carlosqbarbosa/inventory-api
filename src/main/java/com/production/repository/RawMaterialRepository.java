package com.production.repository;

import com.production.entity.RawMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterial> {

    public List<RawMaterial> findByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }

    public List<RawMaterial> findByStockGreaterThan(Integer quantity) {
        return list("stockQuantity > ?1", quantity);
    }

    public List<RawMaterial> findLowStock(Integer threshold) {
        return list("stockQuantity <= ?1 ORDER BY stockQuantity ASC", threshold);
    }
}