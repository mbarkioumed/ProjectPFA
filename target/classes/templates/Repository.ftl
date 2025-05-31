package ${packageName}.repository;

import ${packageName}.entity.${entity.name};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entity.name}Repository extends JpaRepository<${entity.name}, ${entity.idField.type}> {
// Add custom query methods if needed
}