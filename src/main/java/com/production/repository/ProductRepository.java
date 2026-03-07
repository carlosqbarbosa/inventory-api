package com.production.repository;

import com.production.entity.ProductEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<ProductEntity> {

    public List<ProductEntity> findAllWithRawMaterials() {
        return find(
                "SELECT DISTINCT p FROM ProductEntity p " +
                        "LEFT JOIN FETCH p.productRawMaterials prm " +
                        "LEFT JOIN FETCH prm.rawMaterial"
        ).list();
    }

    public ProductEntity findByIdWithRawMaterials(Long id) {
        return find(
                "SELECT p FROM ProductEntity p " +
                        "LEFT JOIN FETCH p.productRawMaterials prm " +
                        "LEFT JOIN FETCH prm.rawMaterial " +
                        "WHERE p.id = ?1",
                id
        ).firstResult();
    }

    public List<ProductEntity> findByName(String name) {

        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }
}