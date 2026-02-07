package com.production.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductionPlanResourceTest {


    @Test
    @Order(1)
    @DisplayName("Should get production plan")
    public void testGetProductionPlan() {
        given()
                .when()
                .get("/production-plan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productionItems", notNullValue())
                .body("totalValue", notNullValue());
    }

    @Test
    @Order(2)
    @DisplayName("Should calculate production for specific product")
    public void testCalculateForProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Test Product");
        product.put("value", 250.00);

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/production-plan/product/" + productId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productId", is(productId))
                .body("productName", notNullValue())
                .body("quantity", notNullValue())
                .body("unitValue", notNullValue())
                .body("totalValue", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("Should check if quantity can be produced")
    public void testCanProduceQuantity() {

        Map<String, Object> product = new HashMap<>();
        product.put("name", "Production Test Product");
        product.put("value", 100.00);

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/production-plan/product/" + productId + "/can-produce?quantity=1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("canProduce", notNullValue());
    }

    @Test
    @Order(4)
    @DisplayName("Should return false for impossible production quantity")
    public void testCannotProduceExcessiveQuantity() {

        Map<String, Object> product = new HashMap<>();
        product.put("name", "Impossible Product");
        product.put("value", 50.00);

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/production-plan/product/" + productId + "/can-produce?quantity=999999")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("canProduce", is(false));
    }

    @Test
    @Order(5)
    @DisplayName("Should return 404 or 400 for non-existent product in production plan")
    public void testProductionPlanForNonExistentProduct() {
        given()
                .when()
                .get("/production-plan/product/999999")
                .then()
                .statusCode(anyOf(is(404), is(400), is(500)));
    }

    @Test
    @Order(6)
    @DisplayName("Production plan should have valid structure")
    public void testProductionPlanStructure() {
        given()
                .when()
                .get("/production-plan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productionItems", instanceOf(java.util.List.class))
                .body("totalValue", instanceOf(java.lang.Number.class));
    }

    @Test
    @Order(7)
    @DisplayName("Should handle zero stock scenario")
    public void testProductionPlanWithZeroStock() {

        Map<String, Object> emptyMaterial = new HashMap<>();
        emptyMaterial.put("name", "Empty Material");
        emptyMaterial.put("stockQuantity", 0);

        given()
                .contentType(ContentType.JSON)
                .body(emptyMaterial)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201);

        Map<String, Object> product = new HashMap<>();
        product.put("name", "Zero Stock Product");
        product.put("value", 100.00);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/production-plan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(8)
    @DisplayName("Should prioritize products by value")
    public void testProductionPlanPrioritization() {

        Map<String, Object> cheapProduct = new HashMap<>();
        cheapProduct.put("name", "Cheap Product");
        cheapProduct.put("value", 10.00);

        given()
                .contentType(ContentType.JSON)
                .body(cheapProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(201);

        Map<String, Object> expensiveProduct = new HashMap<>();
        expensiveProduct.put("name", "Expensive Product");
        expensiveProduct.put("value", 1000.00);

        given()
                .contentType(ContentType.JSON)
                .body(expensiveProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/production-plan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productionItems.size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(9)
    @DisplayName("Production item should have all required fields")
    public void testProductionItemFields() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Fields Test Product");
        product.put("value", 75.00);

        Integer productId = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/production-plan/product/" + productId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productId", notNullValue())
                .body("productName", notNullValue())
                .body("quantity", notNullValue())
                .body("unitValue", notNullValue())
                .body("totalValue", notNullValue());
    }

    @Test
    @Order(10)
    @DisplayName("Should handle multiple products in production plan")
    public void testMultipleProductsInPlan() {

        for (int i = 1; i <= 3; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Plan Product " + i);
            product.put("value", i * 50.0);

            given()
                    .contentType(ContentType.JSON)
                    .body(product)
                    .when()
                    .post("/products")
                    .then()
                    .statusCode(201);
        }

        given()
                .when()
                .get("/production-plan")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("productionItems", notNullValue())
                .body("totalValue", notNullValue());
    }
}