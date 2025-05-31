package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("Product Controller Integration Tests")
class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;
    
    private ProductDto testProductDto;
    private Product savedProduct;
    
    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        
        testProductDto = new ProductDto();
        testProductDto.setPrice(100);
        testProductDto.setName("testName");
        
        // Create a saved entity for tests
        Product entity = new Product();
        entity.setPrice(200);
        entity.setName("existingName");
        savedProduct = productRepository.save(entity);
    }
    
    @Test
    @DisplayName("Should return all entities when GET /api/products")
    void getAllEntities_ReturnsListOfEntities() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(savedProduct.getId().intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }
    
    @Test
    @DisplayName("Should return entity when GET /api/products/{id} with existing ID")
    void getEntityById_ExistingId_ReturnsEntity() throws Exception {
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())));
    }
    
    @Test
    @DisplayName("Should return 404 when GET /api/products/{id} with non-existing ID")
    void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should create entity when POST /api/products with valid data")
    void createProduct_ValidInput_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()));
    }
    
    @Test
    @DisplayName("Should return 400 when POST /api/products with invalid data")
    void createProduct_InvalidInput_ReturnsBadRequest() throws Exception {
        ProductDto invalidDto = new ProductDto();
        // Leave required fields empty to trigger validation errors
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should update entity when PUT /api/products/{id} with valid data")
    void updateProduct_ValidInput_ReturnsUpdated() throws Exception {
        testProductDto.setId(savedProduct.getId());
        
        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(savedProduct.getId().intValue())));
    }
    
    @Test
    @DisplayName("Should return 404 when PUT /api/products/{id} with non-existing ID")
    void updateProduct_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should return 400 when PUT /api/products/{id} with mismatched IDs")
    void updateProduct_MismatchedIds_ReturnsBadRequest() throws Exception {
        testProductDto.setId(999L);
        
        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should delete entity when DELETE /api/products/{id} with existing ID")
    void deleteProduct_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());
        
        // Verify entity is deleted
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should return 404 when DELETE /api/products/{id} with non-existing ID")
    void deleteProduct_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should handle pagination when GET /api/products with page parameters")
    void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
        // Create additional entities for pagination test
        for (int i = 0; i < 5; i++) {
            Product entity = new Product();
            entity.setPrice(100 + i);
            entity.setName("testName" + i);
            productRepository.save(entity);
        }
        
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(3))))
                .andExpect(jsonPath("$.size", is(3)))
                .andExpect(jsonPath("$.totalElements", is(6)))
                .andExpect(jsonPath("$.totalPages", is(2)));
    }
    
    @Test
    @DisplayName("Should return validation errors for invalid JSON")
    void createProduct_MalformedJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}
