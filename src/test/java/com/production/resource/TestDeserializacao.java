package com.production.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.production.entity.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TestDeserializacao {

    public static void main(String[] args) {

        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("name", "Test Product");
            productMap.put("value", 99.99);

            String json = mapper.writeValueAsString(productMap);
            System.out.println("Generated JSON: " + json);

            Product product = mapper.readValue(json, Product.class);

            System.out.println("\n SUCCESS!");
            System.out.println("Name: " + product.getName());
            System.out.println("Price: " + product.getPrice());

        } catch (Exception e) {
            System.out.println("\n Deserialization ERROR!");
            System.out.println("Type: " + e.getClass().getName());
            System.out.println("Message: " + e.getMessage());
            e.printStackTrace();
        }

        try {

            Product product = new Product();
            product.setName("Direct Product");
            product.setPrice(new BigDecimal("150.50"));

            System.out.println(" Product created successfully!");
            System.out.println("Name: " + product.getName());
            System.out.println("Price: " + product.getPrice());

        } catch (Exception e) {
            System.out.println(" ERROR creating Product!");
            e.printStackTrace();
        }

        try {
            Product.class.getDeclaredConstructor();
            System.out.println(" Default constructor exists!");

        } catch (NoSuchMethodException e) {
            System.out.println(" Default constructor does NOT exist!");
            System.out.println("THIS IS THE PROBLEM!");
        }
    }
}
