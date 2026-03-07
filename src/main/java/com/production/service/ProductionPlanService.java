package com.production.service;

import com.production.dto.ProductionItemDTO;
import com.production.dto.ProductionPlanDTO;
import com.production.entity.ProductEntity;
import com.production.entity.ProductRawMaterialEntity;
import com.production.entity.RawMaterialEntity;
import com.production.repository.ProductRepository;
import com.production.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductionPlanService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    public ProductionPlanDTO calculateProductionPlan() {

        ProductionPlanDTO plan = new ProductionPlanDTO();

        List<ProductEntity> products = productRepository.findAllWithRawMaterials();

        products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));

        List<RawMaterialEntity> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, Integer> remainingStock = rawMaterials.stream()
                .collect(Collectors.toMap(
                        RawMaterialEntity::getId,
                        RawMaterialEntity::getStockQuantity
                ));

        for (ProductEntity product : products) {

            int maxQuantity = calculateMaxProductionQuantity(product, remainingStock);

            if (maxQuantity > 0) {

                ProductionItemDTO item = new ProductionItemDTO(
                        product.getId(),
                        product.getName(),
                        maxQuantity,
                        product.getPrice()
                );

                plan.addProductionItem(item);

                updateRemainingStock(product, maxQuantity, remainingStock);
            }
        }

        return plan;
    }

    private int calculateMaxProductionQuantity(
            ProductEntity product,
            Map<Long, Integer> remainingStock
    ) {

        if (product.getProductRawMaterials() == null ||
                product.getProductRawMaterials().isEmpty()) {
            return 0;
        }

        int maxQuantity = Integer.MAX_VALUE;

        for (ProductRawMaterialEntity prm : product.getProductRawMaterials()) {

            Long rawMaterialId = prm.getRawMaterial().getId();
            int requiredPerUnit = prm.getQuantityRequired();
            int available = remainingStock.getOrDefault(rawMaterialId, 0);

            int possible = available / requiredPerUnit;

            maxQuantity = Math.min(maxQuantity, possible);
        }

        return maxQuantity == Integer.MAX_VALUE ? 0 : maxQuantity;
    }

    private void updateRemainingStock(
            ProductEntity product,
            int quantity,
            Map<Long, Integer> remainingStock
    ) {

        for (ProductRawMaterialEntity prm : product.getProductRawMaterials()) {

            Long rawMaterialId = prm.getRawMaterial().getId();
            int totalRequired = prm.getQuantityRequired() * quantity;

            int currentStock = remainingStock.get(rawMaterialId);

            remainingStock.put(
                    rawMaterialId,
                    currentStock - totalRequired
            );
        }
    }

    public ProductionItemDTO calculateProductionForProduct(Long productId) {

        ProductEntity product = productRepository.findByIdWithRawMaterials(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product not found with id: " + productId
            );
        }

        List<RawMaterialEntity> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, Integer> remainingStock = rawMaterials.stream()
                .collect(Collectors.toMap(
                        RawMaterialEntity::getId,
                        RawMaterialEntity::getStockQuantity
                ));

        int maxQuantity = calculateMaxProductionQuantity(product, remainingStock);

        return new ProductionItemDTO(
                product.getId(),
                product.getName(),
                maxQuantity,
                product.getPrice()
        );
    }

    public boolean canProduceQuantity(Long productId, Integer quantity) {

        ProductEntity product = productRepository.findByIdWithRawMaterials(productId);

        if (product == null) {
            return false;
        }

        for (ProductRawMaterialEntity prm : product.getProductRawMaterials()) {

            int required = prm.getQuantityRequired() * quantity;
            int available = prm.getRawMaterial().getStockQuantity();

            if (available < required) {
                return false;
            }
        }

        return true;
    }
}
