package com.production.resource;

import com.production.entity.Product;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductResourceTest {

    @Test
    @Order(1)
    @DisplayName("Should get all products")
    public void testGetAllProducts() {
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(2)
    @DisplayName("Should create a new product")
    public void testCreateProduct() {
        // FIX: Usar Map para evitar problemas de serialização
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Test Product");
        product.put("value", 99.99);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", is("Test Product"))
                .body("value", is(99.99f))
                .body("id", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("Should get product by ID")
    public void testGetProductById() {
        // Create a product
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Product to Get");
        product.put("value", 50.00);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Get it by ID
        given()
                .when()
                .get("/products/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(id))
                .body("name", is("Product to Get"))
                .body("value", is(50.00f));
    }

    @Test
    @Order(4)
    @DisplayName("Should update an existing product")
    public void testUpdateProduct() {
        // Create a product
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Original Name");
        product.put("value", 100.00);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Update the product
        Map<String, Object> updatedProduct = new HashMap<>();
        updatedProduct.put("name", "Updated Name");
        updatedProduct.put("value", 150.00);

        given()
                .contentType(ContentType.JSON)
                .body(updatedProduct)
                .when()
                .put("/products/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("Updated Name"))
                .body("value", is(150.00f));
    }

    @Test
    @Order(5)
    @DisplayName("Should delete a product")
    public void testDeleteProduct() {
        // Create a product
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Product to Delete");
        product.put("value", 75.00);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete the product
        given()
                .when()
                .delete("/products/" + id)
                .then()
                .statusCode(204);

        // Verify it's deleted
        given()
                .when()
                .get("/products/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(6)
    @DisplayName("Should return 400 for invalid product data - empty name")
    public void testCreateProductWithEmptyName() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "");
        product.put("value", 100.00);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    @DisplayName("Should return 400 for invalid product data - negative value")
    public void testCreateProductWithNegativeValue() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Valid Name");
        product.put("value", -10.00);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(8)
    @DisplayName("Should return 404 for non-existent product")
    public void testGetNonExistentProduct() {
        given()
                .when()
                .get("/products/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    @DisplayName("Should return 404 when updating non-existent product")
    public void testUpdateNonExistentProduct() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Updated Name");
        product.put("value", 100.00);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .put("/products/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    @DisplayName("Should return 404 when deleting non-existent product")
    public void testDeleteNonExistentProduct() {
        given()
                .when()
                .delete("/products/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    @DisplayName("Should create product with decimal values correctly")
    public void testCreateProductWithDecimalValue() {
        Map<String, Object> product = new HashMap<>();
        product.put("name", "Decimal Product");
        product.put("value", 123.45);

        given()
                .contentType(ContentType.JSON)
                .body(product)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", is("Decimal Product"))
                .body("value", is(123.45f));
    }

    @Test
    @Order(12)
    @DisplayName("Should list multiple products")
    public void testListMultipleProducts() {
        // Create multiple products
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> product = new HashMap<>();
            product.put("name", "Product " + i);
            product.put("value", i * 10.0);

            given()
                    .contentType(ContentType.JSON)
                    .body(product)
                    .when()
                    .post("/products");
        }

        // Verify list contains products
        given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }
}
