package ${packageName}.controller;

import ${packageName}.dto.${entity.name}Dto;
import ${packageName}.service.${entity.name}Service;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/${entity.namePlural?lower_case}")
public class ${entity.name}Controller {

    private final ${entity.name}Service ${entity.nameLowercase}Service;

    public ${entity.name}Controller(${entity.name}Service ${entity.nameLowercase}Service) {
        this.${entity.nameLowercase}Service = ${entity.nameLowercase}Service;
    }

    @GetMapping
    public ResponseEntity<Page<${entity.name}Dto>> getAll${entity.namePlural}(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<${entity.name}Dto> ${entity.nameLowercase}s = ${entity.nameLowercase}Service.getAll${entity.namePlural}(pageable);
        return ResponseEntity.ok(${entity.nameLowercase}s);
    }

    @GetMapping("/{id}")
    public ResponseEntity<${entity.name}Dto> get${entity.name}ById(@PathVariable ${entity.idField.type} id) {
        return ${entity.nameLowercase}Service.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "${entity.name} not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<${entity.name}Dto> create${entity.name}(@Valid @RequestBody ${entity.name}Dto ${entity.nameLowercase}Dto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (${entity.nameLowercase}Dto.get${entity.idField.name?cap_first}() != null) {
            ${entity.nameLowercase}Dto.set${entity.idField.name?cap_first}(null);
            }
            ${entity.name}Dto savedDto = ${entity.nameLowercase}Service.save(${entity.nameLowercase}Dto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<${entity.name}Dto> update${entity.name}(@PathVariable ${entity.idField.type} id, @Valid @RequestBody ${entity.name}Dto ${entity.nameLowercase}Dto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (${entity.nameLowercase}Dto.get${entity.idField.name?cap_first}() == null) {
                ${entity.nameLowercase}Dto.set${entity.idField.name?cap_first}(id);
                } else if (!${entity.nameLowercase}Dto.get${entity.idField.name?cap_first}().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return ${entity.nameLowercase}Service.update(id, ${entity.nameLowercase}Dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "${entity.name} not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> delete${entity.name}(@PathVariable ${entity.idField.type} id) {
                    if (${entity.nameLowercase}Service.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "${entity.name} not found with id " + id);
                    }
                    }
                    }