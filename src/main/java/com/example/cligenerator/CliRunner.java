package com.example.cligenerator;

import com.example.cligenerator.model.EntityDefinition;
import com.example.cligenerator.model.FieldDefinition;
import com.example.cligenerator.service.CodeGeneratorService;
import com.example.cligenerator.service.InitializrService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class CliRunner implements CommandLineRunner {

    private final InitializrService initializrService;
    private final CodeGeneratorService codeGeneratorService;
    private final Scanner scanner = new Scanner(System.in);

    public CliRunner(InitializrService initializrService, CodeGeneratorService codeGeneratorService) {
        this.initializrService = initializrService;
        this.codeGeneratorService = codeGeneratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Welcome to the Spring Boot API Generator!");

        // --- Spring Initializr Details ---
        System.out.print("Enter project name (e.g., my-api): ");
        String projectName = scanner.nextLine().trim();

        System.out.print("Enter Group ID (e.g., com.example): ");
        String groupId = scanner.nextLine().trim();

        System.out.print("Enter Artifact ID (e.g., demo): ");
        String artifactId = scanner.nextLine().trim();

        String defaultPackage = groupId + "." + artifactId.replace("-", "");
        System.out.print("Enter base package name (default: " + defaultPackage + "): ");
        String packageName = scanner.nextLine().trim();
        if (packageName.isEmpty()) packageName = defaultPackage;

        System.out.print("Enter Java version (e.g., 17, default: 17): ");
        String javaVersion = scanner.nextLine().trim();
        if (javaVersion.isEmpty()) javaVersion = "17";

        System.out.print("Enter Spring Boot version (e.g., 3.1.5, default: 3.1.5): ");
        String springBootVersion = scanner.nextLine().trim();
        if (springBootVersion.isEmpty()) springBootVersion = "3.1.5";

        String defaultDependencies = "web,data-jpa,h2,lombok,validation";
        System.out.print("Enter dependencies (comma-separated, default: " + defaultDependencies + "): ");
        String dependencies = scanner.nextLine().trim();
        if (dependencies.isEmpty()) dependencies = defaultDependencies;

        System.out.print("Enter output directory for the generated project (e.g., ./generated-projects): ");
        String outputDir = scanner.nextLine().trim();
        if (outputDir.isEmpty()) outputDir = "./generated-projects";


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
                System.out.print("Enter field name (or type 'done' if no more fields for " + currentEntity.getName() + "): ");
                String fieldName = scanner.nextLine().trim();
                if ("done".equalsIgnoreCase(fieldName)) {
                    addMoreFields = false;
                    continue;
                }
                if (fieldName.isEmpty() || fieldName.equalsIgnoreCase("id")) {
                    System.out.println("Invalid field name or 'id' (already added). Try again.");
                    continue;
                }

                System.out.print("Enter field type (String, Integer, Long, Double, Boolean, LocalDate, BigDecimal - default: String): ");
                String fieldType = scanner.nextLine().trim();
                if (fieldType.isEmpty()) fieldType = "String";
                // Basic validation for common types
                List<String> validTypes = Arrays.asList("String", "Integer", "Long", "Double", "Boolean", "LocalDate", "BigDecimal");
                if (!validTypes.contains(fieldType)) {
                    System.out.println("Warning: Field type '" + fieldType + "' might require additional imports/configuration.");
                }

                currentEntity.addField(new FieldDefinition(fieldName, fieldType, false));
            }
            entityDefinitions.add(currentEntity);

            System.out.print("Add another entity? (yes/no, default: no): ");
            String addAnotherEntityResponse = scanner.nextLine().trim();
            addMoreEntities = "yes".equalsIgnoreCase(addAnotherEntityResponse);
        }

        if (entityDefinitions.isEmpty()) {
            System.out.println("No entities defined. Exiting.");
            return;
        }

        // --- Generation ---
        System.out.println("\nStarting project generation...");
        Path projectBasePath = initializrService.downloadAndUnzipProject(
                projectName, groupId, artifactId, packageName,
                javaVersion, springBootVersion, dependencies,
                outputDir
        );

        for (EntityDefinition entityDef : entityDefinitions) {
            codeGeneratorService.generateCode(projectBasePath, packageName, entityDef);
        }

        System.out.println("\n------------------------------------------------------------------------");
        System.out.println("SUCCESS: Project '" + projectName + "' generated at: " + projectBasePath.toAbsolutePath());
        System.out.println("To run the generated project:");
        System.out.println("1. cd " + projectBasePath.toAbsolutePath());
        System.out.println("2. ./mvnw spring-boot:run  (or gradlew bootRun if you chose Gradle)");
        System.out.println("------------------------------------------------------------------------");

        scanner.close();
    }
}