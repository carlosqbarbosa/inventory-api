package com.production.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductionPlanDTO {

    private List<ProductionItemDTO> productionItems;
    private BigDecimal totalValue;

    public ProductionPlanDTO() {
        this.productionItems = new ArrayList<>();
        this.totalValue = BigDecimal.ZERO;
    }

    public void addProductionItem(ProductionItemDTO item) {
        this.productionItems.add(item);
        this.totalValue = this.totalValue.add(item.getTotalValue());
    }

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
}