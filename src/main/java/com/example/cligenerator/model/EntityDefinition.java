package com.example.cligenerator.model;

import java.util.ArrayList;
import java.util.List;

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

    public String getName() {
        return this.name;
    }

    public String getNamePlural() {
        return this.namePlural;
    }

    public String getNameLowercase() {
        return this.nameLowercase;
    }

    public List<FieldDefinition> getFields() {
        return this.fields;
    }

    public FieldDefinition getIdField() {
        return this.idField;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamePlural(String namePlural) {
        this.namePlural = namePlural;
    }

    public void setNameLowercase(String nameLowercase) {
        this.nameLowercase = nameLowercase;
    }

    public void setFields(List<FieldDefinition> fields) {
        this.fields = fields;
    }

    public void setIdField(FieldDefinition idField) {
        this.idField = idField;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EntityDefinition)) return false;
        final EntityDefinition other = (EntityDefinition) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$namePlural = this.getNamePlural();
        final Object other$namePlural = other.getNamePlural();
        if (this$namePlural == null ? other$namePlural != null : !this$namePlural.equals(other$namePlural))
            return false;
        final Object this$nameLowercase = this.getNameLowercase();
        final Object other$nameLowercase = other.getNameLowercase();
        if (this$nameLowercase == null ? other$nameLowercase != null : !this$nameLowercase.equals(other$nameLowercase))
            return false;
        final Object this$fields = this.getFields();
        final Object other$fields = other.getFields();
        if (this$fields == null ? other$fields != null : !this$fields.equals(other$fields)) return false;
        final Object this$idField = this.getIdField();
        final Object other$idField = other.getIdField();
        if (this$idField == null ? other$idField != null : !this$idField.equals(other$idField)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EntityDefinition;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $namePlural = this.getNamePlural();
        result = result * PRIME + ($namePlural == null ? 43 : $namePlural.hashCode());
        final Object $nameLowercase = this.getNameLowercase();
        result = result * PRIME + ($nameLowercase == null ? 43 : $nameLowercase.hashCode());
        final Object $fields = this.getFields();
        result = result * PRIME + ($fields == null ? 43 : $fields.hashCode());
        final Object $idField = this.getIdField();
        result = result * PRIME + ($idField == null ? 43 : $idField.hashCode());
        return result;
    }

    public String toString() {
        return "EntityDefinition(name=" + this.getName() + ", namePlural=" + this.getNamePlural() + ", nameLowercase=" + this.getNameLowercase() + ", fields=" + this.getFields() + ", idField=" + this.getIdField() + ")";
    }
}