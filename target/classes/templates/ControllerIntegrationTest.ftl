package ${packageName}.controller;

import ${packageName}.dto.${entity.name}Dto;
import ${packageName}.entity.${entity.name};
import ${packageName}.repository.${entity.name}Repository;
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
@DisplayName("${entity.name} Controller Integration Tests")
class ${entity.name}ControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ${entity.name}Repository ${entity.nameLowercase}Repository;
    
    private ${entity.name}Dto test${entity.name}Dto;
    private ${entity.name} saved${entity.name};
    
    @BeforeEach
    void setUp() {
        ${entity.nameLowercase}Repository.deleteAll();
        
        test${entity.name}Dto = new ${entity.name}Dto();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        test${entity.name}Dto.set${field.name?cap_first}("test${field.name?cap_first}");
    <#elseif field.type == "Integer">
        test${entity.name}Dto.set${field.name?cap_first}(100);
    <#elseif field.type == "Long">
        test${entity.name}Dto.set${field.name?cap_first}(100L);
    <#elseif field.type == "Double">
        test${entity.name}Dto.set${field.name?cap_first}(100.0);
    <#elseif field.type == "Boolean">
        test${entity.name}Dto.set${field.name?cap_first}(true);
    <#elseif field.type == "LocalDate">
        test${entity.name}Dto.set${field.name?cap_first}(java.time.LocalDate.now());
    <#elseif field.type == "BigDecimal">
        test${entity.name}Dto.set${field.name?cap_first}(java.math.BigDecimal.valueOf(100.00));
    </#if>
</#if>
</#list>
        
        // Create a saved entity for tests
        ${entity.name} entity = new ${entity.name}();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        entity.set${field.name?cap_first}("existing${field.name?cap_first}");
    <#elseif field.type == "Integer">
        entity.set${field.name?cap_first}(200);
    <#elseif field.type == "Long">
        entity.set${field.name?cap_first}(200L);
    <#elseif field.type == "Double">
        entity.set${field.name?cap_first}(200.0);
    <#elseif field.type == "Boolean">
        entity.set${field.name?cap_first}(false);
    <#elseif field.type == "LocalDate">
        entity.set${field.name?cap_first}(java.time.LocalDate.now().minusDays(1));
    <#elseif field.type == "BigDecimal">
        entity.set${field.name?cap_first}(java.math.BigDecimal.valueOf(200.00));
    </#if>
</#if>
</#list>
        saved${entity.name} = ${entity.nameLowercase}Repository.save(entity);
    }
    
    @Test
    @DisplayName("Should return all entities when GET /api/${entity.namePlural?lower_case}")
    void getAllEntities_ReturnsListOfEntities() throws Exception {
        mockMvc.perform(get("/api/${entity.namePlural?lower_case}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(saved${entity.name}.getId().intValue())))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }
    
    @Test
    @DisplayName("Should return entity when GET /api/${entity.namePlural?lower_case}/{id} with existing ID")
    void getEntityById_ExistingId_ReturnsEntity() throws Exception {
        mockMvc.perform(get("/api/${entity.namePlural?lower_case}/{id}", saved${entity.name}.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(saved${entity.name}.getId().intValue())));
    }
    
    @Test
    @DisplayName("Should return 404 when GET /api/${entity.namePlural?lower_case}/{id} with non-existing ID")
    void getEntityById_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/${entity.namePlural?lower_case}/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should create entity when POST /api/${entity.namePlural?lower_case} with valid data")
    void create${entity.name}_ValidInput_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/api/${entity.namePlural?lower_case}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test${entity.name}Dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()));
    }
    
    @Test
    @DisplayName("Should return 400 when POST /api/${entity.namePlural?lower_case} with invalid data")
    void create${entity.name}_InvalidInput_ReturnsBadRequest() throws Exception {
        ${entity.name}Dto invalidDto = new ${entity.name}Dto();
        // Leave required fields empty to trigger validation errors
        
        mockMvc.perform(post("/api/${entity.namePlural?lower_case}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should update entity when PUT /api/${entity.namePlural?lower_case}/{id} with valid data")
    void update${entity.name}_ValidInput_ReturnsUpdated() throws Exception {
        test${entity.name}Dto.setId(saved${entity.name}.getId());
        
        mockMvc.perform(put("/api/${entity.namePlural?lower_case}/{id}", saved${entity.name}.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test${entity.name}Dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(saved${entity.name}.getId().intValue())));
    }
    
    @Test
    @DisplayName("Should return 404 when PUT /api/${entity.namePlural?lower_case}/{id} with non-existing ID")
    void update${entity.name}_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(put("/api/${entity.namePlural?lower_case}/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test${entity.name}Dto)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should return 400 when PUT /api/${entity.namePlural?lower_case}/{id} with mismatched IDs")
    void update${entity.name}_MismatchedIds_ReturnsBadRequest() throws Exception {
        test${entity.name}Dto.setId(999L);
        
        mockMvc.perform(put("/api/${entity.namePlural?lower_case}/{id}", saved${entity.name}.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test${entity.name}Dto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should delete entity when DELETE /api/${entity.namePlural?lower_case}/{id} with existing ID")
    void delete${entity.name}_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/${entity.namePlural?lower_case}/{id}", saved${entity.name}.getId()))
                .andExpect(status().isNoContent());
        
        // Verify entity is deleted
        mockMvc.perform(get("/api/${entity.namePlural?lower_case}/{id}", saved${entity.name}.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should return 404 when DELETE /api/${entity.namePlural?lower_case}/{id} with non-existing ID")
    void delete${entity.name}_NonExistingId_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/${entity.namePlural?lower_case}/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Should handle pagination when GET /api/${entity.namePlural?lower_case} with page parameters")
    void getAllEntities_WithPagination_ReturnsPagedResult() throws Exception {
        // Create additional entities for pagination test
        for (int i = 0; i < 5; i++) {
            ${entity.name} entity = new ${entity.name}();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
            entity.set${field.name?cap_first}("test${field.name?cap_first}" + i);
    <#elseif field.type == "Integer">
            entity.set${field.name?cap_first}(100 + i);
    <#elseif field.type == "Long">
            entity.set${field.name?cap_first}(100L + i);
    <#elseif field.type == "Double">
            entity.set${field.name?cap_first}(100.0 + i);
    <#elseif field.type == "Boolean">
            entity.set${field.name?cap_first}(i % 2 == 0);
    <#elseif field.type == "LocalDate">
            entity.set${field.name?cap_first}(java.time.LocalDate.now().minusDays(i));
    <#elseif field.type == "BigDecimal">
            entity.set${field.name?cap_first}(java.math.BigDecimal.valueOf(100.00 + i));
    </#if>
</#if>
</#list>
            ${entity.nameLowercase}Repository.save(entity);
        }
        
        mockMvc.perform(get("/api/${entity.namePlural?lower_case}")
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
    void create${entity.name}_MalformedJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/${entity.namePlural?lower_case}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}
