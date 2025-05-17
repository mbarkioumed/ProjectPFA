package com.demo.service;

import com.demo.dto.ProductDto;
import java.util.List;
import java.util.Optional;

public interface ProductService {
List<ProductDto> findAll();
    Optional<ProductDto> findById(Long id);
        ProductDto save(ProductDto productDto);
        Optional<ProductDto> update(Long id, ProductDto productDto);
            boolean deleteById(Long id);
            }