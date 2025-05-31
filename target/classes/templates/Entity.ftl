package ${packageName}.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
<#if entity.fields?seq_contains("LocalDate") || (entity.fields?map(f -> f.type)?seq_contains("LocalDate")) >
    import java.time.LocalDate;
</#if>
<#if entity.fields?seq_contains("BigDecimal") || (entity.fields?map(f -> f.type)?seq_contains("BigDecimal")) >
    import java.math.BigDecimal;
</#if>

@Entity
@Table(name = "${entity.nameLowercase?lower_case}")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${entity.name} {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private ${entity.idField.type} ${entity.idField.name};

<#list entity.fields as field>
    <#if !field.isId()>
    <#-- Basic validation example, can be expanded -->
        <#if field.type == "String">
            @NotBlank(message = "${field.name?cap_first} cannot be blank")
            @Size(min = 2, max = 255, message = "${field.name?cap_first} must be between 2 and 255 characters")
        </#if>
        <#if field.type == "Integer" || field.type == "Long" || field.type == "Double" || field.type == "BigDecimal">
            @NotNull(message = "${field.name?cap_first} cannot be null")
        </#if>
        <#if field.type == "LocalDate">
            @NotNull(message = "${field.name?cap_first} cannot be null")
            @PastOrPresent(message = "${field.name?cap_first} must be in the past or present")
        </#if>
        private ${field.type} ${field.name};
    </#if>
</#list>

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}