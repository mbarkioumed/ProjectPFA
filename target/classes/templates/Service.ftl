package ${packageName}.service;

import ${packageName}.dto.${entity.name}Dto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface ${entity.name}Service {
    List<${entity.name}Dto> findAll();
    Page<${entity.name}Dto> getAll${entity.namePlural}(Pageable pageable);
    Optional<${entity.name}Dto> findById(${entity.idField.type} id);
    ${entity.name}Dto save(${entity.name}Dto ${entity.nameLowercase}Dto);
    Optional<${entity.name}Dto> update(${entity.idField.type} id, ${entity.name}Dto ${entity.nameLowercase}Dto);
    boolean deleteById(${entity.idField.type} id);
}