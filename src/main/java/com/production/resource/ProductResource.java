package com.production.resource;

import com.production.entity.Product;
import com.production.repository.ProductRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

@Path("/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductRepository repository;

    @GET
    public List<Product> list() {
        return repository.listAll();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Product product = repository.findById(id);

        if (product == null) {
            return Response.status(404).build();
        }

        return Response.ok(product).build();
    }

    @POST
    @Transactional
    public Response create(Product product) {
        try {
            System.out.println("========== CREATE PRODUCT DEBUG ==========");
            System.out.println("Product recebido: " + product);

            if (product == null) {
                System.out.println("ERROR: Product é NULL!");
                return Response.status(400).entity("Product is null").build();
            }

            System.out.println("Name: " + product.getName());
            System.out.println("Value: " + product.getValue());

            if (product.getName() == null || product.getName().isBlank()) {
                System.out.println("ERROR: Name é null ou vazio!");
                return Response.status(400).entity("Name is required").build();
            }

            if (product.getValue() == null) {
                System.out.println("ERROR: Value é NULL!");
                return Response.status(400).entity("Value is required").build();
            }

            if (product.getValue().compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("ERROR: Value é negativo!");
                return Response.status(400).entity("Value must be positive").build();
            }

            System.out.println("Validações OK! Tentando persistir...");
            repository.persist(product);

            System.out.println("Persistido com sucesso! ID: " + product.getId());
            System.out.println("==========================================");

            return Response.status(201).entity(product).build();

        } catch (Exception e) {
            System.out.println("EXCEPTION CAPTURADA!");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
            System.out.println("==========================================");

            return Response.status(500)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Product updated) {
        Product product = repository.findById(id);

        if (product == null) {
            return Response.status(404).build();
        }

        product.setName(updated.getName());
        product.setValue(updated.getValue());

        return Response.ok(product).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            return Response.status(404).build();
        }

        return Response.noContent().build();
    }
}