package com.production.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProductRawMaterialDTO {

    @NotNull(message = "Raw material ID is required")
    private Long rawMaterialId;

    private String rawMaterialName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private Integer stockQuantity;

    public ProductRawMaterialDTO() {
    }

    public ProductRawMaterialDTO(
            Long rawMaterialId,
            String rawMaterialName,
            Integer quantity,
            Integer stockQuantity
    ) {
        this.rawMaterialId = rawMaterialId;
        this.rawMaterialName = rawMaterialName;
        this.quantity = quantity;
        this.stockQuantity = stockQuantity;
    }

    public Long getRawMaterialId() {
        return rawMaterialId;
    }

    public void setRawMaterialId(Long rawMaterialId) {
        this.rawMaterialId = rawMaterialId;
    }

    public String getRawMaterialName() {
        return rawMaterialName;
    }

    public void setRawMaterialName(String rawMaterialName) {
        this.rawMaterialName = rawMaterialName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
