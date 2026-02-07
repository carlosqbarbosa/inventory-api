package com.production.resource;

import com.production.dto.ProductResponseDTO;
import com.production.entity.Product;
import com.production.entity.ProductRawMaterial;
import com.production.entity.RawMaterial;
import com.production.repository.ProductRepository;
import com.production.repository.ProductRawMaterialRepository;
import com.production.repository.RawMaterialRepository;
import com.production.service.ProductService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository productRepository;

    @Inject
    RawMaterialRepository rawMaterialRepository;

    @Inject
    ProductRawMaterialRepository productRawMaterialRepository;

    @Inject
    ProductService productService;

    @GET
    public Response list() {
        List<ProductResponseDTO> products = productRepository.listAll()
                .stream()
                .map(ProductResponseDTO::new)
                .collect(Collectors.toList());
        return Response.ok(products).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Product product = productRepository.findByIdWithRawMaterials(id);

        if (product == null) {
            return Response.status(404).build();
        }

        return Response.ok(new ProductResponseDTO(product)).build();
    }

    @POST
    @Transactional
    public Response create(Product product) {
        try {
            if (product == null) {
                return Response.status(400).entity("Product is null").build();
            }

            if (product.getName() == null || product.getName().isBlank()) {
                return Response.status(400).entity("Name is required").build();
            }

            if (product.getPrice() == null) {
                return Response.status(400).entity("Price is required").build();
            }

            if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return Response.status(400).entity("Price must be positive").build();
            }

            productRepository.persist(product);
            return Response.status(201).entity(new ProductResponseDTO(product)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Product updated) {
        Product product = productRepository.findById(id);

        if (product == null) {
            return Response.status(404).build();
        }

        product.setName(updated.getName());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());

        return Response.ok(new ProductResponseDTO(product)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = productRepository.deleteById(id);

        if (!deleted) {
            return Response.status(404).build();
        }

        return Response.noContent().build();
    }


    @GET
    @Path("/{id}/raw-materials")
    public Response getRawMaterials(@PathParam("id") Long productId) {
        Product product = productRepository.findByIdWithRawMaterials(productId);

        if (product == null) {
            return Response.status(404).entity("Product not found").build();
        }

        return Response.ok(new ProductResponseDTO(product).getProductRawMaterials()).build();
    }

    @POST
    @Path("/{id}/raw-materials")
    @Transactional
    public Response addRawMaterial(@PathParam("id") Long productId, Map<String, Object> payload) {
        try {
            Product product = productRepository.findByIdWithRawMaterials(productId);
            if (product == null) {
                return Response.status(404).entity("Product not found").build();
            }

            Long rawMaterialId = Long.valueOf(payload.get("rawMaterialId").toString());
            Integer quantityRequired = Integer.valueOf(payload.get("quantityRequired").toString());

            RawMaterial rawMaterial = rawMaterialRepository.findById(rawMaterialId);
            if (rawMaterial == null) {
                return Response.status(404).entity("Raw material not found").build();
            }

            boolean exists = product.getProductRawMaterials().stream()
                    .anyMatch(prm -> prm.getRawMaterial().getId().equals(rawMaterialId));

            if (exists) {
                return Response.status(400).entity("Raw material already associated").build();
            }

            ProductRawMaterial productRawMaterial = new ProductRawMaterial();
            productRawMaterial.setProduct(product);
            productRawMaterial.setRawMaterial(rawMaterial);
            productRawMaterial.setQuantityRequired(quantityRequired);

            productRawMaterialRepository.persist(productRawMaterial);

            product = productRepository.findByIdWithRawMaterials(productId);

            return Response.status(201).entity(new ProductResponseDTO(product)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{productId}/raw-materials/{rawMaterialId}")
    @Transactional
    public Response updateRawMaterialQuantity(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId,
            Map<String, Integer> payload) {

        try {
            Product product = productRepository.findByIdWithRawMaterials(productId);
            if (product == null) {
                return Response.status(404).entity("Product not found").build();
            }

            Integer newQuantity = payload.get("quantity");
            if (newQuantity == null || newQuantity <= 0) {
                return Response.status(400).entity("Invalid quantity").build();
            }

            ProductRawMaterial association = product.getProductRawMaterials().stream()
                    .filter(prm -> prm.getRawMaterial().getId().equals(rawMaterialId))
                    .findFirst()
                    .orElse(null);

            if (association == null) {
                return Response.status(404).entity("Association not found").build();
            }

            association.setQuantityRequired(newQuantity);

            product = productRepository.findByIdWithRawMaterials(productId);

            return Response.ok(new ProductResponseDTO(product)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{productId}/raw-materials/{rawMaterialId}")
    @Transactional
    public Response removeRawMaterial(
            @PathParam("productId") Long productId,
            @PathParam("rawMaterialId") Long rawMaterialId) {

        try {
            System.out.println(" Resource: Delegating to ProductService");

            productService.removeRawMaterialFromProduct(productId, rawMaterialId);

            System.out.println(" Resource: Service completed successfully");

            return Response.noContent().build();

        } catch (NotFoundException e) {
            System.out.println(" Resource: Not found - " + e.getMessage());
            return Response.status(404).entity(e.getMessage()).build();

        } catch (Exception e) {
            System.out.println(" Resource: Error - " + e.getMessage());
            e.printStackTrace();
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }
}