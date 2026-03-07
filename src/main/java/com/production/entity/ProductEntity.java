package com.production.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column
    private Integer stock;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductRawMaterialEntity> productRawMaterials = new ArrayList<>();

    public ProductEntity() {}

    public ProductEntity(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("value")
    public BigDecimal getPrice() { return price; }

    @JsonProperty("value")
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public List<ProductRawMaterialEntity> getProductRawMaterials() { return productRawMaterials; }
    public void setProductRawMaterials(List<ProductRawMaterialEntity> productRawMaterials) {
        this.productRawMaterials = productRawMaterials;
    }
}