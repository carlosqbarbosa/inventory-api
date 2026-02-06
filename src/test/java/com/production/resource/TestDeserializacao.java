package com.production.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.production.entity.Product;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TestDeserializacao {

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("TESTE DE DESERIALIZACAO JSON");
        System.out.println("=================================\n");

        ObjectMapper mapper = new ObjectMapper();

        // Teste 1: Usando Map (como o teste faz)
        try {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("name", "Test Product");
            productMap.put("value", 99.99);

            String json = mapper.writeValueAsString(productMap);
            System.out.println("JSON gerado: " + json);

            Product product = mapper.readValue(json, Product.class);

            System.out.println("\n✅ SUCESSO!");
            System.out.println("Nome: " + product.getName());
            System.out.println("Valor: " + product.getValue());

        } catch (Exception e) {
            System.out.println("\n❌ ERRO na deserialização!");
            System.out.println("Tipo: " + e.getClass().getName());
            System.out.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }

        // Teste 2: Usando Product diretamente
        try {
            System.out.println("\n---------------------------------");
            System.out.println("Teste 2: Criar Product direto");
            System.out.println("---------------------------------\n");

            Product product = new Product();
            product.setName("Direct Product");
            product.setValue(new BigDecimal("150.50"));

            System.out.println("✅ Product criado com sucesso!");
            System.out.println("Nome: " + product.getName());
            System.out.println("Valor: " + product.getValue());

        } catch (Exception e) {
            System.out.println("❌ ERRO ao criar Product!");
            e.printStackTrace();
        }

        // Teste 3: Verificar construtor
        try {
            System.out.println("\n---------------------------------");
            System.out.println("Teste 3: Verificar construtor");
            System.out.println("---------------------------------\n");

            Product.class.getDeclaredConstructor();
            System.out.println("✅ Construtor padrão existe!");

        } catch (NoSuchMethodException e) {
            System.out.println("❌ Construtor padrão NÃO existe!");
            System.out.println("ESTE É O PROBLEMA!");
        }
    }
}