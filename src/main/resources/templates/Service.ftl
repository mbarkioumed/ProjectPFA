package ${packageName}.service;

import ${packageName}.dto.${entity.name}Dto;
import java.util.List;
import java.util.Optional;

public interface ${entity.name}Service {
List<${entity.name}Dto> findAll();
    Optional<${entity.name}Dto> findById(${entity.idField.type} id);
        ${entity.name}Dto save(${entity.name}Dto ${entity.nameLowercase}Dto);
        Optional<${entity.name}Dto> update(${entity.idField.type} id, ${entity.name}Dto ${entity.nameLowercase}Dto);
            boolean deleteById(${entity.idField.type} id);
            }