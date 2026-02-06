package com.production.resource;

import com.production.service.ProductionPlanService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/production-plan")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductionPlanResource {

    @Inject
    ProductionPlanService service;

    // Plano completo
    @GET
    public Response getPlan() {
        return Response.ok(service.calculateProductionPlan()).build();
    }

    // Calcular por produto
    @GET
    @Path("/product/{productId}")
    public Response calculateForProduct(@PathParam("productId") Long productId) {

        return Response.ok(
                service.calculateProductionForProduct(productId)
        ).build();
    }

    @GET
    @Path("/product/{productId}/can-produce")
    public Response canProduce(
            @PathParam("productId") Long productId,
            @QueryParam("quantity") Integer quantity) {

        boolean result = service.canProduceQuantity(productId, quantity);

        Map<String, Boolean> response = new HashMap<>();
        response.put("canProduce", result);

        return Response.ok(response).build();
    }
}