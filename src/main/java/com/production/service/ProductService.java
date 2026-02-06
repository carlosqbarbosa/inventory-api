package com.production.service;

import com.production.dto.ProductRawMaterialDTO;
import com.production.entity.Product;
import com.production.entity.ProductRawMaterial;
import com.production.entity.RawMaterial;
import com.production.repository.ProductRepository;
import com.production.repository.ProductRawMaterialRepository;
import com.production.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Inject
    ProductRawMaterialRepository productRawMaterialRepository;

    public List<Product> findAll() {
        return productRepository.listAll();
    }

    public Product findById(Long id) {
        Product product = productRepository.findByIdWithRawMaterials(id);
        if (product == null) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Transactional
    public Product create(Product product) {
        productRepository.persist(product);
        return product;
    }

    @Transactional
    public Product update(Long id, Product updatedProduct) {
        Product product = findById(id);
        product.setName(updatedProduct.getName());
        product.setValue(updatedProduct.getValue());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    // ============================
    // RAW MATERIALS DO PRODUTO
    // ============================

    public List<ProductRawMaterialDTO> getProductRawMaterials(Long productId) {
        Product product = findById(productId);

        return product.getProductRawMaterials()
                .stream()
                .map(prm -> new ProductRawMaterialDTO(
                        prm.getRawMaterial().getId(),
                        prm.getRawMaterial().getName(),
                        prm.getQuantity(),
                        prm.getRawMaterial().getStockQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addRawMaterialToProduct(Long productId, ProductRawMaterialDTO dto) {

        Product product = findById(productId);

        RawMaterial rawMaterial = rawMaterialRepository.findById(dto.getRawMaterialId());

        if (rawMaterial == null) {
            throw new NotFoundException(
                    "Raw material not found with id: " + dto.getRawMaterialId()
            );
        }

        boolean exists = product.getProductRawMaterials()
                .stream()
                .anyMatch(prm ->
                        prm.getRawMaterial().getId().equals(rawMaterial.getId())
                );

        if (exists) {
            throw new IllegalArgumentException(
                    "This raw material is already associated with the product"
            );
        }

        ProductRawMaterial prm = new ProductRawMaterial();
        prm.setProduct(product);
        prm.setRawMaterial(rawMaterial);
        prm.setQuantity(dto.getQuantity());

        product.getProductRawMaterials().add(prm);

        // garante persistência
        productRawMaterialRepository.persist(prm);
    }

    @Transactional
    public void updateRawMaterialQuantity(
            Long productId,
            Long rawMaterialId,
            Integer newQuantity
    ) {

        Product product = findById(productId);

        ProductRawMaterial prm = product.getProductRawMaterials()
                .stream()
                .filter(p ->
                        p.getRawMaterial().getId().equals(rawMaterialId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Raw material not associated with this product")
                );

        prm.setQuantity(newQuantity);
    }

    @Transactional
    public void removeRawMaterialFromProduct(Long productId, Long rawMaterialId) {

        Product product = findById(productId);

        ProductRawMaterial prm = product.getProductRawMaterials()
                .stream()
                .filter(p ->
                        p.getRawMaterial().getId().equals(rawMaterialId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Raw material not associated with this product")
                );

        product.getProductRawMaterials().remove(prm);
        productRawMaterialRepository.delete(prm);
    }
}
