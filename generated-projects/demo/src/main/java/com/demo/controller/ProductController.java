package com.demo.controller;

import com.demo.dto.ProductDto;
import com.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/products") // Simple pluralization
public class ProductController {

private final ProductService productService;

public ProductController(ProductService productService) {
this.productService = productService;
}

@GetMapping
public ResponseEntity<List<ProductDto>> getAllProducts() {
    return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return productService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (productDto.getId() != null) {
            productDto.setId(null);
            }
            ProductDto savedDto = productService.save(productDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto productDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (productDto.getId() == null) {
                productDto.setId(id);
                } else if (!productDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return productService.update(id, productDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
                    if (productService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id " + id);
                    }
                    }
                    }