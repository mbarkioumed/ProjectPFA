package ${packageName}.service;

import ${packageName}.dto.${entity.name}Dto;
import ${packageName}.entity.${entity.name};
import ${packageName}.repository.${entity.name}Repository;
import ${packageName}.service.impl.${entity.name}ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("${entity.name} Service Unit Tests")
class ${entity.name}ServiceTest {
    
    @Mock
    private ${entity.name}Repository ${entity.nameLowercase}Repository;
    
    @InjectMocks
    private ${entity.name}ServiceImpl ${entity.nameLowercase}Service;
    
    private ${entity.name} test${entity.name};
    private ${entity.name}Dto test${entity.name}Dto;
    
    @BeforeEach
    void setUp() {
        test${entity.name} = new ${entity.name}();
        test${entity.name}.setId(1L);
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
        
        test${entity.name}Dto = new ${entity.name}Dto();
        test${entity.name}Dto.setId(1L);
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
    }
    
    @Test
    @DisplayName("Should return all entities when findAll is called")
    void findAll_ReturnsAllEntities() {
        // Given
        List<${entity.name}> entities = Arrays.asList(test${entity.name});
        when(${entity.nameLowercase}Repository.findAll()).thenReturn(entities);
        
        // When
        List<${entity.name}Dto> result = ${entity.nameLowercase}Service.findAll();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(test${entity.name}.getId());
        verify(${entity.nameLowercase}Repository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
        // Given
        when(${entity.nameLowercase}Repository.findAll()).thenReturn(Collections.emptyList());
        
        // When
        List<${entity.name}Dto> result = ${entity.nameLowercase}Service.findAll();
        
        // Then
        assertThat(result).isEmpty();
        verify(${entity.nameLowercase}Repository).findAll();
    }
    
    @Test
    @DisplayName("Should return entity when findById is called with existing ID")
    void findById_ExistingId_ReturnsEntity() {
        // Given
        when(${entity.nameLowercase}Repository.findById(anyLong())).thenReturn(Optional.of(test${entity.name}));
        
        // When
        Optional<${entity.name}Dto> result = ${entity.nameLowercase}Service.findById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(${entity.nameLowercase}Repository).findById(1L);
    }
    
    @Test
    @DisplayName("Should return empty when findById is called with non-existing ID")
    void findById_NonExistingId_ReturnsEmpty() {
        // Given
        when(${entity.nameLowercase}Repository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<${entity.name}Dto> result = ${entity.nameLowercase}Service.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(${entity.nameLowercase}Repository).findById(999L);
    }
    
    @Test
    @DisplayName("Should save entity successfully when valid data is provided")
    void save_ValidEntity_ReturnsSavedEntity() {
        // Given
        when(${entity.nameLowercase}Repository.save(any(${entity.name}.class))).thenReturn(test${entity.name});
        
        // When
        ${entity.name}Dto result = ${entity.nameLowercase}Service.save(test${entity.name}Dto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(test${entity.name}.getId());
        verify(${entity.nameLowercase}Repository).save(any(${entity.name}.class));
    }
    
    @Test
    @DisplayName("Should update entity successfully when valid data is provided")
    void update_ValidEntity_ReturnsUpdatedEntity() {
        // Given
        when(${entity.nameLowercase}Repository.findById(anyLong())).thenReturn(Optional.of(test${entity.name}));
        when(${entity.nameLowercase}Repository.save(any(${entity.name}.class))).thenReturn(test${entity.name});
        
        // When
        Optional<${entity.name}Dto> result = ${entity.nameLowercase}Service.update(1L, test${entity.name}Dto);
        
        // Then
        assertThat(result).isPresent();
        verify(${entity.nameLowercase}Repository).findById(1L);
        verify(${entity.nameLowercase}Repository).save(any(${entity.name}.class));
    }
    
    @Test
    @DisplayName("Should return empty when updating non-existing entity")
    void update_NonExistingEntity_ReturnsEmpty() {
        // Given
        when(${entity.nameLowercase}Repository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<${entity.name}Dto> result = ${entity.nameLowercase}Service.update(999L, test${entity.name}Dto);
        
        // Then
        assertThat(result).isEmpty();
        verify(${entity.nameLowercase}Repository).findById(999L);
        verify(${entity.nameLowercase}Repository, never()).save(any(${entity.name}.class));
    }
    
    @Test
    @DisplayName("Should delete entity successfully when ID exists")
    void deleteById_ExistingId_ReturnsTrue() {
        // Given
        when(${entity.nameLowercase}Repository.existsById(anyLong())).thenReturn(true);
        
        // When
        boolean result = ${entity.nameLowercase}Service.deleteById(1L);
        
        // Then
        assertThat(result).isTrue();
        verify(${entity.nameLowercase}Repository).existsById(1L);
        verify(${entity.nameLowercase}Repository).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existing entity")
    void deleteById_NonExistingId_ReturnsFalse() {
        // Given
        when(${entity.nameLowercase}Repository.existsById(anyLong())).thenReturn(false);
        
        // When
        boolean result = ${entity.nameLowercase}Service.deleteById(999L);
        
        // Then
        assertThat(result).isFalse();
        verify(${entity.nameLowercase}Repository).existsById(999L);
        verify(${entity.nameLowercase}Repository, never()).deleteById(anyLong());
    }
}
