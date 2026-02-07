package com.production.dto;

import com.production.entity.RawMaterial;

public class RawMaterialBasicDTO {

    private Long id;
    private String name;
    private Integer stockQuantity;

    public RawMaterialBasicDTO() {
    }

    public RawMaterialBasicDTO(RawMaterial rawMaterial) {
        this.id = rawMaterial.getId();
        this.name = rawMaterial.getName();
        this.stockQuantity = rawMaterial.getStockQuantity();
    }

    // Getters and Setters
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}