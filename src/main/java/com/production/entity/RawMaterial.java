package com.production.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raw_materials")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer stockQuantity;

    @OneToMany(
            mappedBy = "rawMaterial",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductRawMaterial> productRawMaterials = new ArrayList<>();

    public RawMaterial() {}

    public RawMaterial(String name, Integer stockQuantity) {
        this.name = name;
        this.stockQuantity = stockQuantity;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }


    public void decreaseStock(Integer quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock: " + name);
        }
        stockQuantity -= quantity;
    }

    public void increaseStock(Integer quantity) {
        stockQuantity += quantity;
    }
}
