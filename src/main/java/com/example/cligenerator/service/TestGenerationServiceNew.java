package com.example.cligenerator.service;

import com.example.cligenerator.model.EntityDefinition;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced service for generating comprehensive test classes using FreeMarker
 * templates
 */
@Service
public class TestGenerationServiceNew {

    private static final Logger logger = LoggerFactory.getLogger(TestGenerationServiceNew.class);

    private final TemplateService templateService;

    public TestGenerationServiceNew(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Generate all test classes for an entity with enhanced error handling
     */
    public void generateTestsForEntity(Path projectBasePath, String packageName, EntityDefinition entity) {
        try {
            logger.info("Generating comprehensive tests for entity: {}", entity.getName());
            validateInputs(projectBasePath, packageName, entity);

            generateUnitTests(projectBasePath, packageName, entity);
            generateIntegrationTests(projectBasePath, packageName, entity);

            logger.info("Successfully generated all tests for entity: {}", entity.getName());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input parameters for test generation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to generate tests for entity: {}", entity.getName(), e);
            throw new RuntimeException("Test generation failed for entity: " + entity.getName(), e);
        }
    }

    /**
     * Validate inputs before test generation
     */
    private void validateInputs(Path projectBasePath, String packageName, EntityDefinition entity) {
        if (projectBasePath == null) {
            throw new IllegalArgumentException("Project base path cannot be null");
        }
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be null or empty");
        }
        if (entity == null) {
            throw new IllegalArgumentException("Entity definition cannot be null");
        }
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Entity name cannot be null or empty");
        }
    }

    /**
     * Generate unit tests for service and repository layers
     */
    private void generateUnitTests(Path projectBasePath, String packageName, EntityDefinition entity)
            throws IOException, TemplateException {

        logger.debug("Generating unit tests for entity: {}", entity.getName());

        Map<String, Object> data = createTemplateData(packageName, entity);

        // Service unit test
        Path serviceTestPath = createTestPath(projectBasePath, packageName, "service",
                entity.getName() + "ServiceTest.java");
        generateServiceUnitTest(serviceTestPath, data);

        // Repository test
        Path repositoryTestPath = createTestPath(projectBasePath, packageName, "repository",
                entity.getName() + "RepositoryTest.java");
        generateRepositoryTest(repositoryTestPath, data);
    }

    /**
     * Generate integration tests for controller layer
     */
    private void generateIntegrationTests(Path projectBasePath, String packageName, EntityDefinition entity)
            throws IOException, TemplateException {

        logger.debug("Generating integration tests for entity: {}", entity.getName());

        Map<String, Object> data = createTemplateData(packageName, entity);

        Path controllerTestPath = createTestPath(projectBasePath, packageName, "controller",
                entity.getName() + "ControllerIntegrationTest.java");
        generateControllerIntegrationTest(controllerTestPath, data);
    }

    /**
     * Create standardized test file path
     */
    private Path createTestPath(Path projectBasePath, String packageName, String layer, String fileName) {
        return projectBasePath.resolve("src/test/java")
                .resolve(packageName.replace(".", "/"))
                .resolve(layer)
                .resolve(fileName);
    }

    /**
     * Create comprehensive template data map for test generation
     */
    private Map<String, Object> createTemplateData(String packageName, EntityDefinition entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("packageName", packageName);
        data.put("entity", entity);
        data.put("entityName", entity.getName());
        data.put("entityNameLower", entity.getNameLowercase());
        data.put("entityNamePlural", entity.getNamePlural());
        data.put("entityNamePluralLower", entity.getNamePlural().toLowerCase());
        data.put("fields", entity.getFields());

        // Add utility for test data generation
        data.put("testDataGenerator", new TestDataGenerator());

        return data;
    }

    /**
     * Generate service unit test using enhanced FreeMarker template
     */
    private void generateServiceUnitTest(Path outputPath, Map<String, Object> data)
            throws IOException, TemplateException {

        try {
            String testContent = templateService.processTemplate("ServiceUnitTest.ftl", data);
            writeTestFile(outputPath, testContent);
            logger.debug("Generated service unit test: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate service unit test: {}", outputPath, e);
            throw new TemplateException("Failed to generate service unit test", e, null);
        }
    }

    /**
     * Generate repository test using enhanced FreeMarker template
     */
    private void generateRepositoryTest(Path outputPath, Map<String, Object> data)
            throws IOException, TemplateException {

        try {
            String testContent = templateService.processTemplate("RepositoryTest.ftl", data);
            writeTestFile(outputPath, testContent);
            logger.debug("Generated repository test: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate repository test: {}", outputPath, e);
            throw new TemplateException("Failed to generate repository test", e, null);
        }
    }

    /**
     * Generate controller integration test using enhanced FreeMarker template
     */
    private void generateControllerIntegrationTest(Path outputPath, Map<String, Object> data)
            throws IOException, TemplateException {

        try {
            String testContent = templateService.processTemplate("ControllerIntegrationTest.ftl", data);
            writeTestFile(outputPath, testContent);
            logger.debug("Generated controller integration test: {}", outputPath);
        } catch (Exception e) {
            logger.error("Failed to generate controller integration test: {}", outputPath, e);
            throw new TemplateException("Failed to generate controller integration test", e, null);
        }
    }

    /**
     * Write test file with proper error handling
     */
    private void writeTestFile(Path outputPath, String content) throws IOException {
        try {
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, content);
        } catch (IOException e) {
            logger.error("Failed to write test file: {}", outputPath, e);
            throw new IOException("Failed to write test file: " + outputPath, e);
        }
    }

    /**
     * Utility class for generating test data based on field types
     */
    public static class TestDataGenerator {

        /**
         * Generate appropriate test value based on field type
         */
        public String generateTestValue(String fieldType) {
            return switch (fieldType.toLowerCase()) {
                case "string" -> "\"Test Value\"";
                case "integer", "int" -> "123";
                case "long" -> "123L";
                case "double" -> "123.45";
                case "float" -> "123.45f";
                case "boolean" -> "true";
                case "bigdecimal" -> "new BigDecimal(\"123.45\")";
                case "localdate" -> "LocalDate.of(2023, 1, 1)";
                case "localdatetime" -> "LocalDateTime.of(2023, 1, 1, 12, 0)";
                case "localtime" -> "LocalTime.of(12, 0)";
                case "uuid" -> "UUID.randomUUID()";
                default -> "null";
            };
        }

        /**
         * Generate null test value
         */
        public String generateNullTestValue(String fieldType) {
            return "null";
        }

        /**
         * Generate invalid test value for validation testing
         */
        public String generateInvalidTestValue(String fieldType) {
            return switch (fieldType.toLowerCase()) {
                case "string" -> "\"\""; // empty string
                case "integer", "int" -> "-999";
                case "long" -> "-999L";
                case "double" -> "-999.0";
                case "float" -> "-999.0f";
                case "boolean" -> "false";
                case "bigdecimal" -> "new BigDecimal(\"-999\")";
                case "localdate" -> "LocalDate.of(1900, 1, 1)";
                case "localdatetime" -> "LocalDateTime.of(1900, 1, 1, 0, 0)";
                case "localtime" -> "LocalTime.of(0, 0)";
                default -> "null";
            };
        }

        /**
         * Generate updated test value for update operations
         */
        public String generateUpdatedTestValue(String fieldType) {
            return switch (fieldType.toLowerCase()) {
                case "string" -> "\"Updated Value\"";
                case "integer", "int" -> "456";
                case "long" -> "456L";
                case "double" -> "456.78";
                case "float" -> "456.78f";
                case "boolean" -> "false";
                case "bigdecimal" -> "new BigDecimal(\"456.78\")";
                case "localdate" -> "LocalDate.of(2024, 1, 1)";
                case "localdatetime" -> "LocalDateTime.of(2024, 1, 1, 12, 0)";
                case "localtime" -> "LocalTime.of(15, 30)";
                case "uuid" -> "UUID.randomUUID()";
                default -> "null";
            };
        }

        /**
         * Get required imports for field type
         */
        public String getRequiredImports(String fieldType) {
            return switch (fieldType.toLowerCase()) {
                case "bigdecimal" -> "import java.math.BigDecimal;";
                case "localdate" -> "import java.time.LocalDate;";
                case "localdatetime" -> "import java.time.LocalDateTime;";
                case "localtime" -> "import java.time.LocalTime;";
                case "uuid" -> "import java.util.UUID;";
                default -> "";
            };
        }
    }
}
