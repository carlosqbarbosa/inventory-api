package com.production.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)  // ← MUDANÇA AQUI
    private BigDecimal value;

    @Column
    private Integer stock;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductRawMaterial> productRawMaterials = new ArrayList<>();

    // ===== CONSTRUCTORS =====

    public Product() {
    }

    public Product(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

    // ===== GETTERS E SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<ProductRawMaterial> getProductRawMaterials() {
        return productRawMaterials;
    }

    public void setProductRawMaterials(List<ProductRawMaterial> productRawMaterials) {
        this.productRawMaterials = productRawMaterials;
    }
}