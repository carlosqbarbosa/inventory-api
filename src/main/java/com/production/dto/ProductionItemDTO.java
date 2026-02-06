package com.production.dto;

import java.math.BigDecimal;

public class ProductionItemDTO {

    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;

    // Constructors
    public ProductionItemDTO() {
    }

    public ProductionItemDTO(Long productId, String productName, Integer quantity, BigDecimal unitValue) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitValue = unitValue;
        this.totalValue = unitValue.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.unitValue != null) {
            this.totalValue = this.unitValue.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
        if (this.quantity != null) {
            this.totalValue = unitValue.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}