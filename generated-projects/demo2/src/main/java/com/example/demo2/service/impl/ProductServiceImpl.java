package com.example.demo2.service.impl;

import com.example.demo2.dto.ProductDto;
import com.example.demo2.entity.Product;
import com.example.demo2.repository.ProductRepository;
import com.example.demo2.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class ProductServiceImpl implements ProductService {

private final ProductRepository productRepository;

public ProductServiceImpl(ProductRepository productRepository) {
this.productRepository = productRepository;
}

@Override
@Transactional(readOnly = true)
public List<ProductDto> findAll() {
    return productRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDto> findById(Long id) {
        return productRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public ProductDto save(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (productDto.getId() == null) {
        product.setId(null); // Ensure it's null for auto-generation
        }
        product = productRepository.save(product);
        return convertToDto(product);
        }

        @Override
        public Optional<ProductDto> update(Long id, ProductDto productDto) {
            return productRepository.findById(id)
            .map(existingProduct -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (productDto.getPrice() != null) { // Simple null check
                    existingProduct.setPrice(productDto.getPrice());
                    }
            Product updatedProduct = productRepository.save(existingProduct);
            return convertToDto(updatedProduct);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private ProductDto convertToDto(Product product) {
            ProductDto dto = new ProductDto();
            BeanUtils.copyProperties(product, dto);
            return dto;
            }

            private Product convertToEntity(ProductDto productDto) {
            Product entity = new Product();
            BeanUtils.copyProperties(productDto, entity);
            return entity;
            }
            }