package ${packageName}.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
<#if entity.fields?seq_contains("LocalDate") || (entity.fields?map(f -> f.type)?seq_contains("LocalDate")) >
import java.time.LocalDate;
</#if>
<#if entity.fields?seq_contains("BigDecimal") || (entity.fields?map(f -> f.type)?seq_contains("BigDecimal")) >
import java.math.BigDecimal;
</#if>
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${entity.name}Dto {

    private ${entity.idField.type} ${entity.idField.name};

<#list entity.fields as field>
    <#if !field.isId()>
    <#-- Add validation annotations for DTOs to match entity validation -->
    <#if field.type == "String">
    @NotBlank(message = "${field.name?cap_first} cannot be blank")
    @Size(min = 2, max = 255, message = "${field.name?cap_first} must be between 2 and 255 characters")
    <#else>
    @NotNull(message = "${field.name?cap_first} cannot be null")
    </#if>
    private ${field.type} ${field.name};
    
    </#if>
</#list>

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}