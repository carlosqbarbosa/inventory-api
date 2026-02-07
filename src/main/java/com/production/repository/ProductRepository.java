package com.production.repository;

import com.production.entity.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public List<Product> findAllWithRawMaterials() {
        return find(
                "SELECT DISTINCT p FROM Product p " +
                        "LEFT JOIN FETCH p.productRawMaterials prm " +
                        "LEFT JOIN FETCH prm.rawMaterial"
        ).list();
    }

    public Product findByIdWithRawMaterials(Long id) {
        return find(
                "SELECT p FROM Product p " +
                        "LEFT JOIN FETCH p.productRawMaterials prm " +
                        "LEFT JOIN FETCH prm.rawMaterial " +
                        "WHERE p.id = ?1",
                id
        ).firstResult();
    }

    public List<Product> findByName(String name) {
        return list("LOWER(name) LIKE LOWER(?1)", "%" + name + "%");
    }
}