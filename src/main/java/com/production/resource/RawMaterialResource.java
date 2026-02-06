package com.production.resource;

import com.production.dto.StockRequest;
import com.production.entity.RawMaterial;
import com.production.repository.RawMaterialRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/raw-materials")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RawMaterialResource {

    @Inject
    RawMaterialRepository repository;

    @GET
    public List<RawMaterial> list() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {

        RawMaterial material = repository.findById(id);

        if (material == null) {
            return Response.status(404).build();
        }

        return Response.ok(material).build();
    }

    @POST
    @Transactional
    public Response create(RawMaterial material) {

        if (material.getName() == null || material.getName().isBlank()) {
            return Response.status(400).build();
        }

        if (material.getStockQuantity() < 0) {
            return Response.status(400).build();
        }

        repository.persist(material);
        return Response.status(201).entity(material).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, RawMaterial updated) {

        RawMaterial material = repository.findById(id);

        if (material == null) {
            return Response.status(404).build();
        }

        material.setName(updated.getName());
        material.setStockQuantity(updated.getStockQuantity());

        return Response.ok(material).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {

        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            return Response.status(404).build();
        }

        return Response.noContent().build(); // 204
    }

    @POST
    @Path("/{id}/stock/increase")
    @Transactional
    public Response increase(@PathParam("id") Long id, StockRequest request) {

        RawMaterial material = repository.findById(id);

        if (material == null) return Response.status(404).build();

        if (request == null || request.quantity == null || request.quantity < 0) {
            return Response.status(400).build();
        }

        material.setStockQuantity(material.getStockQuantity() + request.quantity);

        return Response.ok(material).build();
    }

    @POST
    @Path("/{id}/stock/decrease")
    @Transactional
    public Response decrease(@PathParam("id") Long id, StockRequest request) {

        RawMaterial material = repository.findById(id);

        if (material == null) {
            return Response.status(404).build();
        }

        if (request == null || request.quantity == null || request.quantity < 0) {
            return Response.status(400).build();
        }

        if (material.getStockQuantity() < request.quantity) {
            return Response.status(400).build();
        }

        material.setStockQuantity(
                material.getStockQuantity() - request.quantity
        );

        return Response.ok(material).build();
    }
    @PATCH
    @Path("/{id}/stock")
    @Transactional
    public Response updateStock(@PathParam("id") Long id,
                                @QueryParam("quantity") Integer quantity) {

        RawMaterial material = repository.findById(id);

        if (material == null) {
            return Response.status(404).build();
        }

        if (quantity == null || quantity < 0) {
            return Response.status(400).build();
        }

        material.setStockQuantity(quantity);

        return Response.ok(material).build();
    }

    @GET
    @Path("/low-stock")
    public List<RawMaterial> lowStock(@QueryParam("threshold") Integer threshold) {
        return repository.findLowStock(threshold);
    }

    @GET
    @Path("/search")
    public List<RawMaterial> search(@QueryParam("name") String name) {
        return repository.findByName(name);
    }

}
