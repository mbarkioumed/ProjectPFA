package com.example.demo2.service;

import com.example.demo2.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> findAll();
    Page<ProductDto> getAllProducts(Pageable pageable);
    Optional<ProductDto> findById(Long id);
    ProductDto save(ProductDto productDto);
    Optional<ProductDto> update(Long id, ProductDto productDto);
    boolean deleteById(Long id);
}