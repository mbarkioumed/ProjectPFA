package ${packageName}.repository;

import ${packageName}.entity.${entity.name};
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("${entity.name} Repository Tests")
class ${entity.name}RepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private ${entity.name}Repository ${entity.nameLowercase}Repository;
    
    private ${entity.name} test${entity.name};
    
    @BeforeEach
    void setUp() {
        test${entity.name} = new ${entity.name}();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        test${entity.name}.set${field.name?cap_first}("test${field.name?cap_first}");
    <#elseif field.type == "Integer">
        test${entity.name}.set${field.name?cap_first}(100);
    <#elseif field.type == "Long">
        test${entity.name}.set${field.name?cap_first}(100L);
    <#elseif field.type == "Double">
        test${entity.name}.set${field.name?cap_first}(100.0);
    <#elseif field.type == "Boolean">
        test${entity.name}.set${field.name?cap_first}(true);
    <#elseif field.type == "LocalDate">
        test${entity.name}.set${field.name?cap_first}(java.time.LocalDate.now());
    <#elseif field.type == "BigDecimal">
        test${entity.name}.set${field.name?cap_first}(java.math.BigDecimal.valueOf(100.00));
    </#if>
</#if>
</#list>
    }
    
    @Test
    @DisplayName("Should find entity by ID when entity exists")
    void findById_ExistingEntity_ReturnsEntity() {
        // Given
        ${entity.name} savedEntity = entityManager.persistAndFlush(test${entity.name});
        
        // When
        Optional<${entity.name}> result = ${entity.nameLowercase}Repository.findById(savedEntity.getId());
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
<#list entity.fields as field>
<#if !field.isId()>
        assertThat(result.get().get${field.name?cap_first}()).isEqualTo(test${entity.name}.get${field.name?cap_first}());
</#if>
</#list>
    }
    
    @Test
    @DisplayName("Should return empty when finding by non-existing ID")
    void findById_NonExistingEntity_ReturnsEmpty() {
        // When
        Optional<${entity.name}> result = ${entity.nameLowercase}Repository.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should save entity successfully")
    void save_ValidEntity_ReturnsSavedEntity() {
        // When
        ${entity.name} savedEntity = ${entity.nameLowercase}Repository.save(test${entity.name});
        
        // Then
        assertThat(savedEntity.getId()).isNotNull();
<#list entity.fields as field>
<#if !field.isId()>
        assertThat(savedEntity.get${field.name?cap_first}()).isEqualTo(test${entity.name}.get${field.name?cap_first}());
</#if>
</#list>
    }
    
    @Test
    @DisplayName("Should update entity successfully")
    void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
        // Given
        ${entity.name} savedEntity = entityManager.persistAndFlush(test${entity.name});
        entityManager.detach(savedEntity);
        
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        savedEntity.set${field.name?cap_first}("updated${field.name?cap_first}");
    <#elseif field.type == "Integer">
        savedEntity.set${field.name?cap_first}(200);
    <#elseif field.type == "Long">
        savedEntity.set${field.name?cap_first}(200L);
    <#elseif field.type == "Double">
        savedEntity.set${field.name?cap_first}(200.0);
    <#elseif field.type == "Boolean">
        savedEntity.set${field.name?cap_first}(false);
    <#elseif field.type == "LocalDate">
        savedEntity.set${field.name?cap_first}(java.time.LocalDate.now().plusDays(1));
    <#elseif field.type == "BigDecimal">
        savedEntity.set${field.name?cap_first}(java.math.BigDecimal.valueOf(200.00));
    </#if>
</#if>
</#list>
        
        // When
        ${entity.name} updatedEntity = ${entity.nameLowercase}Repository.save(savedEntity);
        
        // Then
        assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
<#list entity.fields as field>
<#if !field.isId()>
        assertThat(updatedEntity.get${field.name?cap_first}()).isEqualTo(savedEntity.get${field.name?cap_first}());
</#if>
</#list>
    }
    
    @Test
    @DisplayName("Should delete entity successfully")
    void deleteById_ExistingEntity_DeletesEntity() {
        // Given
        ${entity.name} savedEntity = entityManager.persistAndFlush(test${entity.name});
        
        // When
        ${entity.nameLowercase}Repository.deleteById(savedEntity.getId());
        
        // Then
        Optional<${entity.name}> result = ${entity.nameLowercase}Repository.findById(savedEntity.getId());
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("Should return all entities")
    void findAll_ReturnsAllEntities() {
        // Given
        ${entity.name} entity1 = entityManager.persistAndFlush(test${entity.name});
        
        ${entity.name} entity2 = new ${entity.name}();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        entity2.set${field.name?cap_first}("another${field.name?cap_first}");
    <#elseif field.type == "Integer">
        entity2.set${field.name?cap_first}(200);
    <#elseif field.type == "Long">
        entity2.set${field.name?cap_first}(200L);
    <#elseif field.type == "Double">
        entity2.set${field.name?cap_first}(200.0);
    <#elseif field.type == "Boolean">
        entity2.set${field.name?cap_first}(false);
    <#elseif field.type == "LocalDate">
        entity2.set${field.name?cap_first}(java.time.LocalDate.now().plusDays(1));
    <#elseif field.type == "BigDecimal">
        entity2.set${field.name?cap_first}(java.math.BigDecimal.valueOf(200.00));
    </#if>
</#if>
</#list>
        entityManager.persistAndFlush(entity2);
        
        // When
        List<${entity.name}> entities = ${entity.nameLowercase}Repository.findAll();
        
        // Then
        assertThat(entities).hasSize(2);
        assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
    }
    
    @Test
    @DisplayName("Should support pagination")
    void findAll_WithPageable_ReturnsPagedResult() {
        // Given
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
            entity.set${field.name?cap_first}(java.time.LocalDate.now().plusDays(i));
    <#elseif field.type == "BigDecimal">
            entity.set${field.name?cap_first}(java.math.BigDecimal.valueOf(100.00 + i));
    </#if>
</#if>
</#list>
            entityManager.persistAndFlush(entity);
        }
        
        // When
        Pageable pageable = PageRequest.of(0, 3);
        Page<${entity.name}> page = ${entity.nameLowercase}Repository.findAll(pageable);
        
        // Then
        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should check if entity exists by ID")
    void existsById_ExistingEntity_ReturnsTrue() {
        // Given
        ${entity.name} savedEntity = entityManager.persistAndFlush(test${entity.name});
        
        // When
        boolean exists = ${entity.nameLowercase}Repository.existsById(savedEntity.getId());
        
        // Then
        assertThat(exists).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when entity does not exist")
    void existsById_NonExistingEntity_ReturnsFalse() {
        // When
        boolean exists = ${entity.nameLowercase}Repository.existsById(999L);
        
        // Then
        assertThat(exists).isFalse();
    }
    
    @Test
    @DisplayName("Should return correct count of entities")
    void count_ReturnsCorrectCount() {
        // Given
        entityManager.persistAndFlush(test${entity.name});
        
        ${entity.name} entity2 = new ${entity.name}();
<#list entity.fields as field>
<#if !field.isId()>
    <#if field.type == "String">
        entity2.set${field.name?cap_first}("another${field.name?cap_first}");
    <#elseif field.type == "Integer">
        entity2.set${field.name?cap_first}(200);
    <#elseif field.type == "Long">
        entity2.set${field.name?cap_first}(200L);
    <#elseif field.type == "Double">
        entity2.set${field.name?cap_first}(200.0);
    <#elseif field.type == "Boolean">
        entity2.set${field.name?cap_first}(false);
    <#elseif field.type == "LocalDate">
        entity2.set${field.name?cap_first}(java.time.LocalDate.now().plusDays(1));
    <#elseif field.type == "BigDecimal">
        entity2.set${field.name?cap_first}(java.math.BigDecimal.valueOf(200.00));
    </#if>
</#if>
</#list>
        entityManager.persistAndFlush(entity2);
        
        // When
        long count = ${entity.nameLowercase}Repository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }
}
