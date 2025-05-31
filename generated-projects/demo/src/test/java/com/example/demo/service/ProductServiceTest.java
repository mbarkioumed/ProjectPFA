package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.impl.ProductServiceImpl;
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
@DisplayName("Product Service Unit Tests")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product testProduct;
    private ProductDto testProductDto;
    
    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setPrice(100);
        testProduct.setName("testName");
        
        testProductDto = new ProductDto();
        testProductDto.setId(1L);
        testProductDto.setPrice(100);
        testProductDto.setName("testName");
    }
    
    @Test
    @DisplayName("Should return all entities when findAll is called")
    void findAll_ReturnsAllEntities() {
        // Given
        List<Product> entities = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(entities);
        
        // When
        List<ProductDto> result = productService.findAll();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(testProduct.getId());
        verify(productRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
        // Given
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        
        // When
        List<ProductDto> result = productService.findAll();
        
        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return entity when findById is called with existing ID")
    void findById_ExistingId_ReturnsEntity() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<ProductDto> result = productService.findById(1L);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(productRepository).findById(1L);
    }
    
    @Test
    @DisplayName("Should return empty when findById is called with non-existing ID")
    void findById_NonExistingId_ReturnsEmpty() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<ProductDto> result = productService.findById(999L);
        
        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findById(999L);
    }
    
    @Test
    @DisplayName("Should save entity successfully when valid data is provided")
    void save_ValidEntity_ReturnsSavedEntity() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductDto result = productService.save(testProductDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testProduct.getId());
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should update entity successfully when valid data is provided")
    void update_ValidEntity_ReturnsUpdatedEntity() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        Optional<ProductDto> result = productService.update(1L, testProductDto);
        
        // Then
        assertThat(result).isPresent();
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should return empty when updating non-existing entity")
    void update_NonExistingEntity_ReturnsEmpty() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When
        Optional<ProductDto> result = productService.update(999L, testProductDto);
        
        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should delete entity successfully when ID exists")
    void deleteById_ExistingId_ReturnsTrue() {
        // Given
        when(productRepository.existsById(anyLong())).thenReturn(true);
        
        // When
        boolean result = productService.deleteById(1L);
        
        // Then
        assertThat(result).isTrue();
        verify(productRepository).existsById(1L);
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should return false when deleting non-existing entity")
    void deleteById_NonExistingId_ReturnsFalse() {
        // Given
        when(productRepository.existsById(anyLong())).thenReturn(false);
        
        // When
        boolean result = productService.deleteById(999L);
        
        // Then
        assertThat(result).isFalse();
        verify(productRepository).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }
}
