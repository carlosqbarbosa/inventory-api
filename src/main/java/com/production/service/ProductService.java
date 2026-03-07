package com.production.service;

import com.production.dto.ProductRawMaterialDTO;
import com.production.entity.ProductEntity;
import com.production.entity.ProductRawMaterialEntity;
import com.production.entity.RawMaterialEntity;
import com.production.repository.ProductRepository;
import com.production.repository.ProductRawMaterialRepository;
import com.production.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
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

    @Inject
    EntityManager entityManager;

    public List<ProductEntity> findAll() {
        return productRepository.listAll();
    }

    public ProductEntity findById(Long id) {
        ProductEntity product = productRepository.findByIdWithRawMaterials(id);
        if (product == null) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Transactional
    public ProductEntity create(ProductEntity product) {
        productRepository.persist(product);
        return product;
    }

    @Transactional
    public ProductEntity update(Long id, ProductEntity updatedProduct) {
        ProductEntity product = findById(id);
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        return product;
    }

    @Transactional
    public void delete(Long id) {
        ProductEntity product = findById(id);
        productRepository.delete(product);
    }

    public List<ProductRawMaterialDTO> getProductRawMaterials(Long productId) {
        ProductEntity product = findById(productId);

        return product.getProductRawMaterials()
                .stream()
                .map(prm -> new ProductRawMaterialDTO(
                        prm.getRawMaterial().getId(),
                        prm.getRawMaterial().getName(),
                        prm.getQuantityRequired(),
                        prm.getRawMaterial().getStockQuantity()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addRawMaterialToProduct(Long productId, ProductRawMaterialDTO dto) {

        ProductEntity product = findById(productId);

        RawMaterialEntity rawMaterial = rawMaterialRepository.findById(dto.getRawMaterialId());

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

        ProductRawMaterialEntity prm = new ProductRawMaterialEntity();
        prm.setProduct(product);
        prm.setRawMaterial(rawMaterial);
        prm.setQuantityRequired(dto.getQuantityRequired());

        product.getProductRawMaterials().add(prm);
        productRawMaterialRepository.persist(prm);
    }

    @Transactional
    public void updateRawMaterialQuantity(
            Long productId,
            Long rawMaterialId,
            Integer newQuantity
    ) {

        ProductEntity product = findById(productId);

        ProductRawMaterialEntity prm = product.getProductRawMaterials()
                .stream()
                .filter(p ->
                        p.getRawMaterial().getId().equals(rawMaterialId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Raw material not associated with this product")
                );

        prm.setQuantityRequired(newQuantity);
    }

    @Transactional
    public void removeRawMaterialFromProduct(Long productId, Long rawMaterialId) {

        System.out.println(" Backend DELETE - Start: productId=" + productId + ", rawMaterialId=" + rawMaterialId);

        ProductEntity product = findById(productId);

        System.out.println(" Product found: " + product.getName() + ", materials count: " + product.getProductRawMaterials().size());

        ProductRawMaterialEntity prm = product.getProductRawMaterials()
                .stream()
                .filter(p -> p.getRawMaterial().getId().equals(rawMaterialId))
                .findFirst()
                .orElseThrow(() ->
                        new NotFoundException("Raw material not associated with this product")
                );

        System.out.println(" Found relationship to remove: " + prm.getId());

        product.getProductRawMaterials().remove(prm);

        productRawMaterialRepository.delete(prm);

        entityManager.flush();

        entityManager.refresh(product);

        System.out.println(" Backend DELETE - Complete. Remaining materials: " + product.getProductRawMaterials().size());
    }
}