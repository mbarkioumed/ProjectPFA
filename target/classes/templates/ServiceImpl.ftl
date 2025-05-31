package ${packageName}.service.impl;

import ${packageName}.dto.${entity.name}Dto;
import ${packageName}.entity.${entity.name};
import ${packageName}.repository.${entity.name}Repository;
import ${packageName}.service.${entity.name}Service;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class ${entity.name}ServiceImpl implements ${entity.name}Service {

private final ${entity.name}Repository ${entity.nameLowercase}Repository;

public ${entity.name}ServiceImpl(${entity.name}Repository ${entity.nameLowercase}Repository) {
this.${entity.nameLowercase}Repository = ${entity.nameLowercase}Repository;
}

@Override
@Transactional(readOnly = true)
public List<${entity.name}Dto> findAll() {
    return ${entity.nameLowercase}Repository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<${entity.name}Dto> getAll${entity.namePlural}(Pageable pageable) {
        return ${entity.nameLowercase}Repository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<${entity.name}Dto> findById(${entity.idField.type} id) {
        return ${entity.nameLowercase}Repository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public ${entity.name}Dto save(${entity.name}Dto ${entity.nameLowercase}Dto) {
        ${entity.name} ${entity.nameLowercase} = convertToEntity(${entity.nameLowercase}Dto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (${entity.nameLowercase}Dto.get${entity.idField.name?cap_first}() == null) {
        ${entity.nameLowercase}.set${entity.idField.name?cap_first}(null); // Ensure it's null for auto-generation
        }
        ${entity.nameLowercase} = ${entity.nameLowercase}Repository.save(${entity.nameLowercase});
        return convertToDto(${entity.nameLowercase});
        }

        @Override
        public Optional<${entity.name}Dto> update(${entity.idField.type} id, ${entity.name}Dto ${entity.nameLowercase}Dto) {
            return ${entity.nameLowercase}Repository.findById(id)
            .map(existing${entity.name} -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
            <#list entity.fields as field>
                <#if !field.isId()>
                    if (${entity.nameLowercase}Dto.get${field.name?cap_first}() != null) { // Simple null check
                    existing${entity.name}.set${field.name?cap_first}(${entity.nameLowercase}Dto.get${field.name?cap_first}());
                    }
                </#if>
            </#list>
            ${entity.name} updated${entity.name} = ${entity.nameLowercase}Repository.save(existing${entity.name});
            return convertToDto(updated${entity.name});
            });
            }


            @Override
            public boolean deleteById(${entity.idField.type} id) {
            if (${entity.nameLowercase}Repository.existsById(id)) {
            ${entity.nameLowercase}Repository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private ${entity.name}Dto convertToDto(${entity.name} ${entity.nameLowercase}) {
            ${entity.name}Dto dto = new ${entity.name}Dto();
            BeanUtils.copyProperties(${entity.nameLowercase}, dto);
            return dto;
            }

            private ${entity.name} convertToEntity(${entity.name}Dto ${entity.nameLowercase}Dto) {
            ${entity.name} entity = new ${entity.name}();
            BeanUtils.copyProperties(${entity.nameLowercase}Dto, entity);
            return entity;
            }
            }