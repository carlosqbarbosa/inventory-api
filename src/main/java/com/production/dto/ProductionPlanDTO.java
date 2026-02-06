package com.production.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlanDTO {

    private List<ProductionItemDTO> productionItems = new ArrayList<>();
    private BigDecimal totalValue = BigDecimal.ZERO;

    // Constructors
    public ProductionPlanDTO() {
    }

    public ProductionPlanDTO(List<ProductionItemDTO> productionItems, BigDecimal totalValue) {
        this.productionItems = productionItems;
        this.totalValue = totalValue;
    }

    // Getters and Setters
    public List<ProductionItemDTO> getProductionItems() {
        return productionItems;
    }

    public void setProductionItems(List<ProductionItemDTO> productionItems) {
        this.productionItems = productionItems;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    // Helper methods
    public void addProductionItem(ProductionItemDTO item) {
        this.productionItems.add(item);
        this.totalValue = this.totalValue.add(item.getTotalValue());
    }
}