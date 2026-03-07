package com.production.service;

import com.production.entity.RawMaterialEntity;
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

    public List<RawMaterialEntity> findAll() {
        return rawMaterialRepository.listAll();
    }

    public RawMaterialEntity findById(Long id) {
        RawMaterialEntity rawMaterial = rawMaterialRepository.findById(id);
        if (rawMaterial == null) {
            throw new NotFoundException("Raw material not found with id: " + id);
        }
        return rawMaterial;
    }

    public List<RawMaterialEntity> searchByName(String name) {
        return rawMaterialRepository.findByName(name);
    }

    @Transactional
    public RawMaterialEntity create(RawMaterialEntity rawMaterial) {
        rawMaterialRepository.persist(rawMaterial);
        return rawMaterial;
    }

    @Transactional
    public RawMaterialEntity update(Long id, RawMaterialEntity updatedRawMaterial) {
        RawMaterialEntity rawMaterial = findById(id);
        rawMaterial.setName(updatedRawMaterial.getName());
        rawMaterial.setStockQuantity(updatedRawMaterial.getStockQuantity());
        return rawMaterial;
    }

    @Transactional
    public void delete(Long id) {
        RawMaterialEntity rawMaterial = findById(id);
        rawMaterialRepository.delete(rawMaterial);
    }

    @Transactional
    public RawMaterialEntity updateStock(Long id, Integer newQuantity) {
        RawMaterialEntity rawMaterial = findById(id);
        rawMaterial.setStockQuantity(newQuantity);
        return rawMaterial;
    }

    @Transactional
    public RawMaterialEntity increaseStock(Long id, Integer quantity) {
        RawMaterialEntity rawMaterial = findById(id);
        rawMaterial.increaseStock(quantity);
        return rawMaterial;
    }

    @Transactional
    public RawMaterialEntity decreaseStock(Long id, Integer quantity) {
        RawMaterialEntity rawMaterial = findById(id);
        rawMaterial.decreaseStock(quantity);
        return rawMaterial;
    }

    public List<RawMaterialEntity> findLowStock(Integer threshold) {
        return rawMaterialRepository.findLowStock(threshold);
    }
}
