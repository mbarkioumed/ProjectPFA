package com.example.cligenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinition {
    private String name;
    private String type; // e.g., "String", "Long", "Integer", "LocalDate"
    private boolean isId = false; // To mark the ID field
}