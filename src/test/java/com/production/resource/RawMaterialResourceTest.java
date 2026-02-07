package com.production.resource;

import com.production.entity.RawMaterial;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RawMaterialResourceTest {

    @Test
    @Order(1)
    @DisplayName("Should get all raw materials")
    public void testGetAllRawMaterials() {
        given()
                .when()
                .get("/raw-materials")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(2)
    @DisplayName("Should create a new raw material")
    public void testCreateRawMaterial() {
        RawMaterial material = new RawMaterial("Steel", 100);

        given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("name", is("Steel"))
                .body("stockQuantity", is(100))
                .body("id", notNullValue());
    }

    @Test
    @Order(3)
    @DisplayName("Should get raw material by ID")
    public void testGetRawMaterialById() {
        RawMaterial material = new RawMaterial("Plastic", 50);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/raw-materials/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(id))
                .body("name", is("Plastic"))
                .body("stockQuantity", is(50));
    }

    @Test
    @Order(4)
    @DisplayName("Should update raw material")
    public void testUpdateRawMaterial() {
        RawMaterial material = new RawMaterial("Aluminum", 200);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        RawMaterial updatedMaterial = new RawMaterial("Updated Aluminum", 300);

        given()
                .contentType(ContentType.JSON)
                .body(updatedMaterial)
                .when()
                .put("/raw-materials/" + id)
                .then()
                .statusCode(200)
                .body("name", is("Updated Aluminum"))
                .body("stockQuantity", is(300));
    }

    @Test
    @Order(5)
    @DisplayName("Should delete raw material")
    public void testDeleteRawMaterial() {
        RawMaterial material = new RawMaterial("To Delete", 50);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .delete("/raw-materials/" + id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/raw-materials/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(6)
    @DisplayName("Should update stock quantity")
    public void testUpdateStock() {
        RawMaterial material = new RawMaterial("Copper", 100);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .patch("/raw-materials/" + id + "/stock?quantity=150")
                .then()
                .statusCode(200)
                .body("stockQuantity", is(150));
    }

    @Test
    @Order(7)
    @DisplayName("Should increase stock")
    public void testIncreaseStock() {
        RawMaterial material = new RawMaterial("Bronze", 100);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        Map<String, Integer> request = new HashMap<>();
        request.put("quantity", 50);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/raw-materials/" + id + "/stock/increase")
                .then()
                .statusCode(200)
                .body("stockQuantity", is(150));
    }

    @Test
    @Order(8)
    @DisplayName("Should decrease stock")
    public void testDecreaseStock() {
        RawMaterial material = new RawMaterial("Gold", 100);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        Map<String, Integer> request = new HashMap<>();
        request.put("quantity", 30);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/raw-materials/" + id + "/stock/decrease")
                .then()
                .statusCode(200)
                .body("stockQuantity", is(70));
    }

    @Test
    @Order(9)
    @DisplayName("Should return 400 for invalid raw material - empty name")
    public void testCreateRawMaterialWithEmptyName() {
        RawMaterial material = new RawMaterial("", 100);

        given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(10)
    @DisplayName("Should return 400 for invalid raw material - negative stock")
    public void testCreateRawMaterialWithNegativeStock() {
        RawMaterial material = new RawMaterial("Valid Name", -10);

        given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(11)
    @DisplayName("Should get low stock materials")
    public void testGetLowStock() {
        given()
                .contentType(ContentType.JSON)
                .body(new RawMaterial("Low Stock 1", 5))
                .when()
                .post("/raw-materials");

        given()
                .contentType(ContentType.JSON)
                .body(new RawMaterial("Low Stock 2", 8))
                .when()
                .post("/raw-materials");

        given()
                .contentType(ContentType.JSON)
                .body(new RawMaterial("High Stock", 100))
                .when()
                .post("/raw-materials");

        given()
                .when()
                .get("/raw-materials/low-stock?threshold=10")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(12)
    @DisplayName("Should search materials by name")
    public void testSearchByName() {
        given()
                .contentType(ContentType.JSON)
                .body(new RawMaterial("Titanium Alloy", 50))
                .when()
                .post("/raw-materials");

        given()
                .when()
                .get("/raw-materials/search?name=Titanium")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0));
    }

    @Test
    @Order(13)
    @DisplayName("Should return 404 for non-existent material")
    public void testGetNonExistentMaterial() {
        given()
                .when()
                .get("/raw-materials/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(14)
    @DisplayName("Should create material with zero stock")
    public void testCreateMaterialWithZeroStock() {
        RawMaterial material = new RawMaterial("Empty Stock", 0);

        given()
                .contentType(ContentType.JSON)
                .body(material)
                .when()
                .post("/raw-materials")
                .then()
                .statusCode(201)
                .body("stockQuantity", is(0));
    }
}