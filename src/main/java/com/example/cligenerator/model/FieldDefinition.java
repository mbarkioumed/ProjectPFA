package com.example.cligenerator.model;

public class FieldDefinition {
    private String name;
    private String type; // e.g., "String", "Long", "Integer", "LocalDate"
    private boolean isId = false; // To mark the ID field

    public FieldDefinition(String name, String type, boolean isId) {
        this.name = name;
        this.type = type;
        this.isId = isId;
    }

    public FieldDefinition() {
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public boolean isId() {
        return this.isId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(boolean isId) {
        this.isId = isId;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof FieldDefinition)) return false;
        final FieldDefinition other = (FieldDefinition) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        if (this.isId() != other.isId()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof FieldDefinition;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        result = result * PRIME + (this.isId() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "FieldDefinition(name=" + this.getName() + ", type=" + this.getType() + ", isId=" + this.isId() + ")";
    }
}