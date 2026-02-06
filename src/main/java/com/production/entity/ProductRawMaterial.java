package com.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "product_raw_materials")
public class ProductRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer quantity;

    // ===== CONSTRUCTORS =====

    public ProductRawMaterial() {}

    public ProductRawMaterial(Product product, RawMaterial rawMaterial, Integer quantity) {
        this.product = product;
        this.rawMaterial = rawMaterial;
        this.quantity = quantity;
    }

    // ===== GETTERS E SETTERS =====

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
