package com.production.service;

import com.production.entity.RawMaterial;
import com.production.repository.RawMaterialRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class RawMaterialService {

    @Inject
    RawMaterialRepository rawMaterialRepository;

    public List<RawMaterial> findAll() {
        return rawMaterialRepository.listAll();
    }

    public RawMaterial findById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Raw material not found with id: " + id);
        }
        return rawMaterial;
    }

    public List<RawMaterial> searchByName(String name) {
        return rawMaterialRepository.findByName(name);
    }

    @Transactional
    public RawMaterial create(RawMaterial rawMaterial) {
        rawMaterialRepository.persist(rawMaterial);
        return rawMaterial;
    }

    @Transactional
    public RawMaterial update(Long id, RawMaterial updatedRawMaterial) {
        RawMaterial rawMaterial = findById(id);
        rawMaterial.setName(updatedRawMaterial.getName());
        rawMaterial.setStockQuantity(updatedRawMaterial.getStockQuantity());
        return rawMaterial;
    }

    @Transactional
    public void delete(Long id) {
        RawMaterial rawMaterial = findById(id);
        rawMaterialRepository.delete(rawMaterial);
    }

    @Transactional
    public RawMaterial updateStock(Long id, Integer newQuantity) {
        RawMaterial rawMaterial = findById(id);
        rawMaterial.setStockQuantity(newQuantity);
        return rawMaterial;
    }

    @Transactional
    public RawMaterial increaseStock(Long id, Integer quantity) {
        RawMaterial rawMaterial = findById(id);
        rawMaterial.increaseStock(quantity);
        return rawMaterial;
    }

    @Transactional
    public RawMaterial decreaseStock(Long id, Integer quantity) {
        RawMaterial rawMaterial = findById(id);
        rawMaterial.decreaseStock(quantity);
        return rawMaterial;
    }

    public List<RawMaterial> findLowStock(Integer threshold) {
        return rawMaterialRepository.findLowStock(threshold);
    }
}
