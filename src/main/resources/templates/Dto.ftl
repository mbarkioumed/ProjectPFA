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
import jakarta.validation.constraints.*; // For annotations if you copy them here too

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${entity.name}Dto {

private ${entity.idField.type} ${entity.idField.name};

<#list entity.fields as field>
    <#if !field.isId()>
        private ${field.type} ${field.name};
    </#if>
</#list>

// Lombok will generate getters, setters, constructor, toString, equals, hashCode
}