package com.production.repository;

import com.production.entity.ProductRawMaterialEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRawMaterialRepository implements PanacheRepository<ProductRawMaterialEntity> {

    public List<ProductRawMaterialEntity> findByProductId(Long productId) {
        return list("product.id", productId);
    }

    public List<ProductRawMaterialEntity> findByRawMaterialId(Long rawMaterialId) {
        return list("rawMaterial.id", rawMaterialId);
    }

    public void deleteByProductId(Long productId) {
        delete("product.id", productId);
    }

    public void deleteByProductIdAndRawMaterialId(Long productId, Long rawMaterialId) {
        delete("product.id = ?1 AND rawMaterial.id = ?2", productId, rawMaterialId);
    }
}