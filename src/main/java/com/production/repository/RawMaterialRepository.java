package com.production.repository;

import com.production.entity.RawMaterialEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RawMaterialRepository implements PanacheRepository<RawMaterialEntity> {

    public List<RawMaterialEntity> findByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }

    public List<RawMaterialEntity> findByStockGreaterThan(Integer quantity) {
        return list("stockQuantity > ?1", quantity);
    }

    public List<RawMaterialEntity> findLowStock(Integer threshold) {
        return list("stockQuantity <= ?1 ORDER BY stockQuantity ASC", threshold);
    }
}