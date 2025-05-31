package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

            @NotNull(message = "Price cannot be null")
        private Integer price;
            @NotBlank(message = "Name cannot be blank")
            @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        private String name;

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}