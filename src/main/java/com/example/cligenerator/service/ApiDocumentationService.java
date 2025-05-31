package com.example.cligenerator.service;

import com.example.cligenerator.model.EntityDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating advanced API documentation and configuration
 */
@Service
public class ApiDocumentationService {

    private static final Logger logger = LoggerFactory.getLogger(ApiDocumentationService.class);

    private final TemplateService templateService;

    public ApiDocumentationService(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Generate enhanced OpenAPI configuration with entity-specific tags
     */
    public void generateEnhancedOpenApiConfig(Path projectBasePath, String packageName,
            String projectName, java.util.List<EntityDefinition> entities)
            throws IOException {

        logger.info("Generating enhanced OpenAPI configuration for project: {}", projectName);

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", packageName);
        data.put("projectName", projectName);
        data.put("entities", entities);

        try {
            String configContent = templateService.processTemplate("EnhancedOpenApiConfig.ftl", data);

            Path configPath = projectBasePath.resolve("src/main/java")
                    .resolve(packageName.replace(".", "/"))
                    .resolve("config")
                    .resolve("OpenApiConfig.java");

            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, configContent);

            logger.debug("Generated enhanced OpenAPI config: {}", configPath);
        } catch (Exception e) {
            logger.error("Failed to generate enhanced OpenAPI config", e);
            throw new IOException("Failed to generate enhanced OpenAPI configuration", e);
        }
    }

    /**
     * Generate global exception handler for consistent error responses
     */
    public void generateGlobalExceptionHandler(Path projectBasePath, String packageName)
            throws IOException {

        logger.info("Generating global exception handler");

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", packageName);

        try {
            String handlerContent = templateService.processTemplate("GlobalExceptionHandler.ftl", data);

            Path handlerPath = projectBasePath.resolve("src/main/java")
                    .resolve(packageName.replace(".", "/"))
                    .resolve("exception")
                    .resolve("GlobalExceptionHandler.java");

            Files.createDirectories(handlerPath.getParent());
            Files.writeString(handlerPath, handlerContent);

            logger.debug("Generated global exception handler: {}", handlerPath);
        } catch (Exception e) {
            logger.error("Failed to generate global exception handler", e);
            throw new IOException("Failed to generate global exception handler", e);
        }
    }

    /**
     * Generate ResourceNotFoundException for consistent not found errors
     */
    public void generateResourceNotFoundException(Path projectBasePath, String packageName)
            throws IOException {

        logger.info("Generating ResourceNotFoundException");

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", packageName);

        try {
            String exceptionContent = templateService.processTemplate("ResourceNotFoundException.ftl", data);

            Path exceptionPath = projectBasePath.resolve("src/main/java")
                    .resolve(packageName.replace(".", "/"))
                    .resolve("exception")
                    .resolve("ResourceNotFoundException.java");

            Files.createDirectories(exceptionPath.getParent());
            Files.writeString(exceptionPath, exceptionContent);

            logger.debug("Generated ResourceNotFoundException: {}", exceptionPath);
        } catch (Exception e) {
            logger.error("Failed to generate ResourceNotFoundException", e);
            throw new IOException("Failed to generate ResourceNotFoundException", e);
        }
    }

    /**
     * Generate comprehensive API documentation package
     */
    public void generateApiDocumentation(Path projectBasePath, String packageName,
            String projectName, java.util.List<EntityDefinition> entities)
            throws IOException {

        logger.info("Generating comprehensive API documentation package");

        try {
            generateEnhancedOpenApiConfig(projectBasePath, packageName, projectName, entities);
            // Removed GlobalExceptionHandler and ResourceNotFoundException generation
            // as they were causing complications and are not essential

            logger.info("Successfully generated API documentation package");
        } catch (Exception e) {
            logger.error("Failed to generate API documentation package", e);
            throw new IOException("Failed to generate complete API documentation package", e);
        }
    }
}
