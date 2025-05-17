package com.example.cligenerator.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class EntityDefinition {
    private String name; // e.g., "Product"
    private String namePlural; // e.g., "Products"
    private String nameLowercase; // e.g., "product"
    private List<FieldDefinition> fields = new ArrayList<>();
    private FieldDefinition idField;

    public EntityDefinition(String name) {
        this.name = name;
        this.nameLowercase = name.toLowerCase();
        // Simple pluralization, can be improved
        this.namePlural = name.endsWith("s") ? name + "es" : name + "s";
    }

    public void addField(FieldDefinition field) {
        this.fields.add(field);
        if (field.isId()) {
            this.idField = field;
        }
    }
}