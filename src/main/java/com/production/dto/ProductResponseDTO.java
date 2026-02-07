package com.production.dto;

import com.production.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ProductResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private List<ProductRawMaterialResponseDTO> productRawMaterials;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.productRawMaterials = product.getProductRawMaterials()
                .stream()
                .map(ProductRawMaterialResponseDTO::new)
                .collect(Collectors.toList());
    }

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public List<ProductRawMaterialResponseDTO> getProductRawMaterials() {
        return productRawMaterials;
    }

    public void setProductRawMaterials(List<ProductRawMaterialResponseDTO> productRawMaterials) {
        this.productRawMaterials = productRawMaterials;
    }
}