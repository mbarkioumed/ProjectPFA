package com.example.demo2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;

    @NotNull(message = "Price cannot be null")
    private Integer price;
    

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}