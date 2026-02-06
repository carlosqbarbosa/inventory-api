package com.production.service;

import com.production.dto.ProductionItemDTO;
import com.production.dto.ProductionPlanDTO;
import com.production.entity.Product;
import com.production.entity.ProductRawMaterial;
import com.production.entity.RawMaterial;
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

        List<Product> products = productRepository.findAllWithRawMaterials();

        // Prioriza por valor (maior primeiro)
        products.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));

        List<RawMaterial> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, Integer> remainingStock = rawMaterials.stream()
                .collect(Collectors.toMap(
                        RawMaterial::getId,
                        RawMaterial::getStockQuantity
                ));

        for (Product product : products) {

            int maxQuantity = calculateMaxProductionQuantity(product, remainingStock);

            if (maxQuantity > 0) {

                ProductionItemDTO item = new ProductionItemDTO(
                        product.getId(),
                        product.getName(),
                        maxQuantity,
                        product.getValue()
                );

                plan.addProductionItem(item);

                updateRemainingStock(product, maxQuantity, remainingStock);
            }
        }

        return plan;
    }

    private int calculateMaxProductionQuantity(
            Product product,
            Map<Long, Integer> remainingStock
    ) {

        if (product.getProductRawMaterials() == null ||
                product.getProductRawMaterials().isEmpty()) {
            return 0;
        }

        int maxQuantity = Integer.MAX_VALUE;

        for (ProductRawMaterial prm : product.getProductRawMaterials()) {

            Long rawMaterialId = prm.getRawMaterial().getId();
            int requiredPerUnit = prm.getQuantity();
            int available = remainingStock.getOrDefault(rawMaterialId, 0);

            int possible = available / requiredPerUnit;

            maxQuantity = Math.min(maxQuantity, possible);
        }

        return maxQuantity == Integer.MAX_VALUE ? 0 : maxQuantity;
    }

    private void updateRemainingStock(
            Product product,
            int quantity,
            Map<Long, Integer> remainingStock
    ) {

        for (ProductRawMaterial prm : product.getProductRawMaterials()) {

            Long rawMaterialId = prm.getRawMaterial().getId();
            int totalRequired = prm.getQuantity() * quantity;

            int currentStock = remainingStock.get(rawMaterialId);

            remainingStock.put(
                    rawMaterialId,
                    currentStock - totalRequired
            );
        }
    }

    public ProductionItemDTO calculateProductionForProduct(Long productId) {

        Product product = productRepository.findByIdWithRawMaterials(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product not found with id: " + productId
            );
        }

        List<RawMaterial> rawMaterials = rawMaterialRepository.listAll();

        Map<Long, Integer> remainingStock = rawMaterials.stream()
                .collect(Collectors.toMap(
                        RawMaterial::getId,
                        RawMaterial::getStockQuantity
                ));

        int maxQuantity = calculateMaxProductionQuantity(product, remainingStock);

        return new ProductionItemDTO(
                product.getId(),
                product.getName(),
                maxQuantity,
                product.getValue()
        );
    }

    public boolean canProduceQuantity(Long productId, Integer quantity) {

        Product product = productRepository.findByIdWithRawMaterials(productId);

        if (product == null) {
            return false;
        }

        for (ProductRawMaterial prm : product.getProductRawMaterials()) {

            int required = prm.getQuantity() * quantity;
            int available = prm.getRawMaterial().getStockQuantity();

            if (available < required) {
                return false;
            }
        }

        return true;
    }
}
