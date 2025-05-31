package com.example.cligenerator;

import com.example.cligenerator.exception.GenerationException;
import com.example.cligenerator.model.DatabaseConfig;
import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.FieldDefinition;
import com.example.cligenerator.service.ApiDocumentationService;
import com.example.cligenerator.service.CodeGeneratorService;
import com.example.cligenerator.service.ConfigurationService;
import com.example.cligenerator.service.DockerService;
import com.example.cligenerator.service.InitializrService;
import com.example.cligenerator.service.TestGenerationService;
import com.example.cligenerator.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class CliRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CliRunner.class);
    private final InitializrService initializrService;
    private final CodeGeneratorService codeGeneratorService;
    private final TestGenerationService testGenerationService;
    private final DockerService dockerService;
    private final TemplateService templateService;
    private final ApiDocumentationService apiDocumentationService;
    private final ConfigurationService configurationService;
    private final Scanner scanner = new Scanner(System.in);

    public CliRunner(InitializrService initializrService,
            CodeGeneratorService codeGeneratorService,
            TestGenerationService testGenerationService,
            DockerService dockerService,
            TemplateService templateService,
            ApiDocumentationService apiDocumentationService,
            ConfigurationService configurationService) {
        this.initializrService = initializrService;
        this.codeGeneratorService = codeGeneratorService;
        this.testGenerationService = testGenerationService;
        this.dockerService = dockerService;
        this.templateService = templateService;
        this.apiDocumentationService = apiDocumentationService;
        this.configurationService = configurationService;
    }

    @Override
    public void run(String... args) throws Exception {
        ConfigurationService.ProjectConfiguration config = null;

        try {
            logger.info("Starting Enhanced Spring Boot API Generator");
            System.out.println("=== Enhanced Spring Boot API Generator ===");

            // Load configuration
            Path configPath = configurationService.getDefaultConfigPath();
            config = configurationService.loadConfiguration(configPath);

            if (!configurationService.validateConfiguration(config)) {
                logger.warn("Configuration validation failed, using defaults");
                config = new ConfigurationService.ProjectConfiguration();
            }

            // Check for custom templates
            System.out.print("Use custom templates? Enter directory path (or press Enter to use defaults): ");
            String customTemplateDir = scanner.nextLine().trim();
            if (!customTemplateDir.isEmpty()) {
                Path templateDir = Path.of(customTemplateDir);
                if (Files.exists(templateDir)) {
                    templateService.loadCustomTemplates(templateDir);
                    System.out.println("✓ Loaded custom templates from: " + templateDir);
                } else {
                    System.out.println("⚠ Custom template directory not found, using defaults");
                }
            } // --- Enhanced Project Details with Validation ---
            System.out.println("\n--- Project Configuration ---");

            String projectName = getValidatedInput("Enter project name (e.g., my-api): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9-_]+"),
                    "Project name must contain only letters, numbers, hyphens, and underscores");

            String groupId = getValidatedInput("Enter Group ID (e.g., com.example): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9._-]+"),
                    "Group ID must be a valid package name format");

            String artifactId = getValidatedInput("Enter Artifact ID (e.g., demo): ",
                    input -> !input.trim().isEmpty() && input.matches("[a-zA-Z0-9-_]+"),
                    "Artifact ID must contain only letters, numbers, hyphens, and underscores");

            String defaultPackage = groupId + "." + artifactId.replace("-", "");
            System.out.print("Enter base package name (default: " + defaultPackage + "): ");
            String packageName = scanner.nextLine().trim();
            if (packageName.isEmpty()) {
                packageName = defaultPackage;
            } else if (!packageName.matches("[a-zA-Z0-9._]+")) {
                throw new GenerationException("Input Validation", "Invalid package name format",
                        "Package name must be a valid Java package name");
            }

            String javaVersion = getInputWithDefault("Enter Java version (default: " +
                    config.getDefaultValues().get("javaVersion") + "): ",
                    config.getDefaultValues().get("javaVersion"));

            String springBootVersion = getInputWithDefault("Enter Spring Boot version (default: " +
                    config.getDefaultValues().get("springBootVersion") + "): ",
                    config.getDefaultValues().get("springBootVersion"));

            String dependencies = "web,data-jpa,lombok,validation";

            // Enhanced Database Selection
            System.out.println("\n--- Database Configuration ---");
            System.out.println("Choose a database:");
            DatabaseConfig.DatabaseType[] dbTypes = DatabaseConfig.DatabaseType.values();
            for (int i = 0; i < dbTypes.length; i++) {
                System.out.println((i + 1) + ". " + dbTypes[i].getDisplayName());
            }
            System.out.print("Enter your choice (1-" + dbTypes.length + ", default: 1 for H2): ");
            String dbChoice = scanner.nextLine().trim();

            DatabaseConfig.DatabaseType selectedDbType = DatabaseConfig.DatabaseType.H2; // default
            if (!dbChoice.isEmpty()) {
                try {
                    int choice = Integer.parseInt(dbChoice);
                    if (choice >= 1 && choice <= dbTypes.length) {
                        selectedDbType = dbTypes[choice - 1];
                    } else {
                        System.out.println("Invalid choice. Using H2 as default.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Using H2 as default.");
                }
            }

            System.out.println("Selected database: " + selectedDbType.getDisplayName());
            dependencies += "," + selectedDbType.getSpringInitializrName();

            // Create database configuration
            DatabaseConfig databaseConfig = DatabaseConfig.createDefaultConfig(selectedDbType, projectName);

            if (selectedDbType != DatabaseConfig.DatabaseType.H2) {
                System.out.println("\nWarning: You selected " + selectedDbType.getDisplayName() + ".");
                System.out.println("Make sure you have the database server running and accessible.");
                System.out.println("The generated application.properties will contain placeholder connection details.");
            } // Add OpenAPI support
            System.out.print("Include OpenAPI/Swagger documentation? (yes/no, default: yes): ");
            String includeOpenApi = scanner.nextLine().trim();
            boolean generateOpenApi = includeOpenApi.isEmpty() || "yes".equalsIgnoreCase(includeOpenApi);

            // Note: We'll add OpenAPI dependency manually to pom.xml after project
            // generation
            // since Spring Initializr doesn't support the springdoc dependency directly

            System.out.print("Enter dependencies to add (comma-separated. Already chosen: " + dependencies + "): ");
            String deps = scanner.nextLine().trim();
            if (!deps.isEmpty())
                dependencies = dependencies + "," + deps;

            System.out.print("Enter output directory for the generated project (e.g., ./generated-projects): ");
            String outputDir = scanner.nextLine().trim();
            if (outputDir.isEmpty())
                outputDir = "./generated-projects";

            // --- Entity Definitions ---
            List<EntityDefinition> entityDefinitions = new ArrayList<>();
            boolean addMoreEntities = true;
            while (addMoreEntities) {
                System.out.println("\n--- Define Entity ---");
                System.out.print("Enter entity name (e.g., Product, User - singular, capitalized): ");
                String entityNameInput = scanner.nextLine().trim();
                if (entityNameInput.isEmpty()) {
                    System.out.println("Entity name cannot be empty. Skipping this entity.");
                    continue;
                }
                EntityDefinition currentEntity = new EntityDefinition(entityNameInput);

                // Add default ID field
                currentEntity.addField(new FieldDefinition("id", "Long", true));

                boolean addMoreFields = true;
                while (addMoreFields) {
                    System.out.print(
                            "Enter field name (or type 'done' if no more fields for " + currentEntity.getName()
                                    + "): ");
                    String fieldName = scanner.nextLine().trim();
                    if ("done".equalsIgnoreCase(fieldName)) {
                        addMoreFields = false;
                        continue;
                    }
                    if (fieldName.isEmpty() || fieldName.equalsIgnoreCase("id")) {
                        System.out.println("Invalid field name or 'id' (already added). Try again.");
                        continue;
                    }

                    System.out.print(
                            "Enter field type (String, Integer, Long, Double, Boolean, LocalDate, BigDecimal - default: String): ");
                    String fieldType = scanner.nextLine().trim();
                    if (fieldType.isEmpty())
                        fieldType = "String";
                    // Basic validation for common types
                    List<String> validTypes = Arrays.asList("String", "Integer", "Long", "Double", "Boolean",
                            "LocalDate",
                            "BigDecimal");
                    if (!validTypes.contains(fieldType)) {
                        System.out.println(
                                "Warning: Field type '" + fieldType
                                        + "' might require additional imports/configuration.");
                    }

                    currentEntity.addField(new FieldDefinition(fieldName, fieldType, false));
                }
                entityDefinitions.add(currentEntity);

                System.out.print("Add another entity? (yes/no, default: no): ");
                String addAnotherEntityResponse = scanner.nextLine().trim();
                addMoreEntities = "yes".equalsIgnoreCase(addAnotherEntityResponse);
            }

            if (entityDefinitions.isEmpty()) {
                throw new GenerationException("Entity Configuration", "No entities defined",
                        "At least one entity is required for code generation");
            }

            // --- Additional Features ---
            System.out.print("Generate Docker configuration? (yes/no, default: yes): ");
            String generateDocker = scanner.nextLine().trim();
            boolean includeDocker = generateDocker.isEmpty() || "yes".equalsIgnoreCase(generateDocker);

            System.out.print("Generate comprehensive tests? (yes/no, default: yes): ");
            String generateTests = scanner.nextLine().trim();
            boolean includeTests = generateTests.isEmpty() || "yes".equalsIgnoreCase(generateTests);

            // --- Generation ---
            System.out.println("\nStarting project generation...");
            logger.info("Generating project: {}", projectName);

            Path projectBasePath;
            try {
                projectBasePath = initializrService.downloadAndUnzipProject(
                        projectName, groupId, artifactId, packageName,
                        javaVersion, springBootVersion, dependencies,
                        outputDir);
            } catch (Exception e) {
                throw new GenerationException("Spring Initializr", "Failed to generate base project",
                        "Error downloading from Spring Initializr: " + e.getMessage(), e);
            }

            // Find the actual package where the main class was created
            String actualPackageName = findMainClassPackage(projectBasePath, artifactId);
            if (actualPackageName != null) {
                packageName = actualPackageName;
                System.out.println("Using detected package: " + packageName);
            }

            try {
                // Generate application.properties with database configuration
                codeGeneratorService.generateApplicationProperties(projectBasePath, databaseConfig);

                // Generate entity code
                for (EntityDefinition entityDef : entityDefinitions) {
                    logger.info("Generating code for entity: {}", entityDef.getName());
                    codeGeneratorService.generateCode(projectBasePath, packageName, entityDef);
                } // Generate comprehensive API documentation if requested
                if (generateOpenApi) {
                    System.out.println("Adding OpenAPI dependency...");
                    codeGeneratorService.addOpenApiDependency(projectBasePath);
                    System.out.println("Generating comprehensive API documentation...");
                    apiDocumentationService.generateApiDocumentation(projectBasePath, packageName, projectName,
                            entityDefinitions);
                }

                // Generate Docker configuration if requested
                if (includeDocker) {
                    System.out.println("Generating Docker configuration...");
                    dockerService.generateDockerConfiguration(projectBasePath, artifactId, javaVersion, databaseConfig);
                }

                // Generate tests if requested
                if (includeTests) {
                    System.out.println("Generating comprehensive tests...");
                    for (EntityDefinition entityDef : entityDefinitions) {
                        testGenerationService.generateTestsForEntity(projectBasePath, packageName, entityDef);
                    }
                }

            } catch (Exception e) {
                throw new GenerationException("Code Generation", "Failed to generate project files",
                        "Error during code generation: " + e.getMessage(), e);
            }
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ SUCCESS: Enhanced project '" + projectName + "' generated at: "
                    + projectBasePath.toAbsolutePath());
            System.out.println("\n📦 Generated components:");
            System.out.println("✓ Spring Boot application structure with best practices");
            System.out.println("✓ Entity classes with JPA annotations and DTOs");
            System.out.println("✓ Repository, Service, and Controller layers with pagination");
            System.out.println("✓ Database configuration with connection pooling");

            if (generateOpenApi) {
                System.out.println("✓ Comprehensive OpenAPI/Swagger documentation with:");
                System.out.println("  • Enhanced API information and contact details");
                System.out.println("  • Global exception handling with structured error responses");
                System.out.println("  • Resource-specific tags and operation descriptions");
                System.out.println("  📊 API documentation: http://localhost:8080/swagger-ui.html");
            }

            if (includeDocker) {
                System.out.println("✓ Production-ready Docker configuration:");
                System.out.println("  • Multi-stage Dockerfile with optimization");
                System.out.println("  • Docker Compose with database service");
                System.out.println("  • Environment-specific configurations");
            }

            if (includeTests) {
                System.out.println("✓ Comprehensive test suite:");
                System.out.println("  • Unit tests for service layer with Mockito");
                System.out.println("  • Integration tests for controllers with MockMvc");
                System.out.println("  • Repository tests with @DataJpaTest");
                System.out.println("  • Test data generators for all field types");
            }

            System.out.println("\n🚀 Quick Start:");
            System.out.println("1. cd " + projectBasePath.toAbsolutePath());
            System.out.println("2. ./mvnw spring-boot:run");

            if (includeDocker) {
                System.out.println("\n🐳 Docker Quick Start:");
                System.out.println("1. ./mvnw clean package");
                System.out.println("2. docker-compose up -d");
            }

            System.out.println("\n📝 Next Steps:");
            System.out.println("• Update application.properties with your database configuration");
            System.out.println("• Customize validation annotations in entity classes");
            System.out.println("• Add business logic to service implementations");
            System.out.println("• Configure security if needed");
            System.out.println("=".repeat(80));

        } catch (GenerationException e) {
            logger.error("Generation failed in {}: {}", e.getComponent(), e.getMessage(), e);
            System.err.println("❌ Generation failed in " + e.getComponent() + ": " + e.getMessage());
            System.err.println("Details: " + e.getDetails());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error during generation", e);
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            scanner.close();
        }
    }

    private String findMainClassPackage(Path projectBasePath, String artifactId) {
        try {
            String mainClassName = toCamelCase(artifactId) + "Application";
            Path srcPath = projectBasePath.resolve("src/main/java");

            return Files.walk(srcPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(mainClassName + ".java"))
                    .findFirst()
                    .map(path -> {
                        Path relativePath = srcPath.relativize(path.getParent());
                        return relativePath.toString().replace('/', '.').replace('\\', '.');
                    })
                    .orElse(null);
        } catch (Exception e) {
            System.out.println("Could not detect main class package, using provided package name");
            return null;
        }
    }

    private String toCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == '-' || c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Get validated input with custom validation function
     */
    private String getValidatedInput(String prompt, java.util.function.Predicate<String> validator,
            String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (validator.test(input)) {
                return input;
            }
            System.out.println("❌ " + errorMessage + " Please try again.");
        }
    }

    /**
     * Get input with default value
     */
    private String getInputWithDefault(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    /**
     * Get boolean input with default
     */
    private boolean getBooleanInput(String prompt, boolean defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        return "yes".equalsIgnoreCase(input) || "y".equalsIgnoreCase(input) || "true".equalsIgnoreCase(input);
    }
}