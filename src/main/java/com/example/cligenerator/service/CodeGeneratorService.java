package com.example.cligenerator.service;

import com.example.cligenerator.model.DatabaseConfig;
import com.example.cligenerator.model.EntityDefinition;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    private final Configuration freemarkerConfig;

    public CodeGeneratorService(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public void generateCode(Path projectBasePath, String basePackage, EntityDefinition entityDef)
            throws IOException, TemplateException {
        String packagePath = basePackage.replace('.', '/');
        Path javaSrcPath = projectBasePath.resolve("src/main/java").resolve(packagePath);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", basePackage);
        dataModel.put("entity", entityDef);
        dataModel.put("idType", entityDef.getIdField() != null ? entityDef.getIdField().getType() : "Long");

        // Ensure directories exist
        Files.createDirectories(javaSrcPath.resolve("entity"));
        Files.createDirectories(javaSrcPath.resolve("repository"));
        Files.createDirectories(javaSrcPath.resolve("service"));
        Files.createDirectories(javaSrcPath.resolve("service/impl"));
        Files.createDirectories(javaSrcPath.resolve("controller"));
        Files.createDirectories(javaSrcPath.resolve("dto"));

        // Generate Entity
        generateFile(dataModel, "Entity.ftl", javaSrcPath.resolve("entity/" + entityDef.getName() + ".java"));
        // Generate DTO
        generateFile(dataModel, "Dto.ftl", javaSrcPath.resolve("dto/" + entityDef.getName() + "Dto.java"));
        // Generate Repository
        generateFile(dataModel, "Repository.ftl",
                javaSrcPath.resolve("repository/" + entityDef.getName() + "Repository.java"));
        // Generate Service Interface
        generateFile(dataModel, "Service.ftl", javaSrcPath.resolve("service/" + entityDef.getName() + "Service.java"));
        // Generate Service Implementation
        generateFile(dataModel, "ServiceImpl.ftl",
                javaSrcPath.resolve("service/impl/" + entityDef.getName() + "ServiceImpl.java"));
        // Generate Controller
        generateFile(dataModel, "Controller.ftl",
                javaSrcPath.resolve("controller/" + entityDef.getName() + "Controller.java"));

        System.out.println("Generated code for entity: " + entityDef.getName());
    }

    public void generateApplicationProperties(Path projectBasePath, DatabaseConfig databaseConfig) throws IOException {
        Path applicationPropertiesPath = projectBasePath.resolve("src/main/resources/application.properties");

        try (Writer writer = new FileWriter(applicationPropertiesPath.toFile())) {
            writer.write(databaseConfig.generateApplicationProperties());
        }

        System.out.println("Generated: " + applicationPropertiesPath.toAbsolutePath());
    }

    public void generateOpenApiConfig(Path projectBasePath, String basePackage, String projectName)
            throws IOException, TemplateException {
        String packagePath = basePackage.replace('.', '/');
        Path javaSrcPath = projectBasePath.resolve("src/main/java").resolve(packagePath);

        // Ensure config directory exists
        Files.createDirectories(javaSrcPath.resolve("config"));

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("packageName", basePackage);
        dataModel.put("projectName", projectName);

        Path configPath = javaSrcPath.resolve("config/OpenApiConfig.java");
        generateFile(dataModel, "OpenApiConfig.ftl", configPath);

        System.out.println("Generated OpenAPI configuration: " + configPath.toAbsolutePath());
    }

    public void addOpenApiDependency(Path projectBasePath) throws IOException {
        Path pomPath = projectBasePath.resolve("pom.xml");

        if (!Files.exists(pomPath)) {
            throw new IOException("pom.xml not found in generated project");
        }

        // Read the existing pom.xml
        String pomContent = Files.readString(pomPath);

        // OpenAPI dependency to add
        String openApiDependency = """
                <dependency>
                	<groupId>org.springdoc</groupId>
                	<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
                	<version>2.2.0</version>
                </dependency>""";

        // Find the end of dependencies section and insert before it
        if (pomContent.contains("</dependencies>")) {
            pomContent = pomContent.replace("</dependencies>", openApiDependency + "\n\t</dependencies>");
            Files.writeString(pomPath, pomContent);
            System.out.println("Added OpenAPI dependency to: " + pomPath.toAbsolutePath());
        } else {
            throw new IOException("Could not find </dependencies> tag in pom.xml");
        }
    }

    private void generateFile(Map<String, Object> dataModel, String templateName, Path outputPath)
            throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName);
        try (Writer fileWriter = new FileWriter(outputPath.toFile())) {
            template.process(dataModel, fileWriter);
        }
        System.out.println("Generated: " + outputPath.toAbsolutePath());
    }
}