package com.production.dto;

import com.production.entity.ProductRawMaterial;

public class ProductRawMaterialResponseDTO {

    private Long id;
    private RawMaterialBasicDTO rawMaterial;
    private Integer quantityRequired;

    public ProductRawMaterialResponseDTO() {
    }

    public ProductRawMaterialResponseDTO(ProductRawMaterial prm) {
        this.id = prm.getId();
        this.rawMaterial = new RawMaterialBasicDTO(prm.getRawMaterial());
        this.quantityRequired = prm.getQuantityRequired();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RawMaterialBasicDTO getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterialBasicDTO rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public Integer getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Integer quantityRequired) {
        this.quantityRequired = quantityRequired;
    }
}