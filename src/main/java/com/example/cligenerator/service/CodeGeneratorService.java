package com.example.cligenerator.service;

import com.example.cligenerator.model.EntityDefinition;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class CodeGeneratorService {

    private final Configuration freemarkerConfig;

    public CodeGeneratorService(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
    }

    public void generateCode(Path projectBasePath, String basePackage, EntityDefinition entityDef) throws IOException, TemplateException {
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
        generateFile(dataModel, "Repository.ftl", javaSrcPath.resolve("repository/" + entityDef.getName() + "Repository.java"));
        // Generate Service Interface
        generateFile(dataModel, "Service.ftl", javaSrcPath.resolve("service/" + entityDef.getName() + "Service.java"));
        // Generate Service Implementation
        generateFile(dataModel, "ServiceImpl.ftl", javaSrcPath.resolve("service/impl/" + entityDef.getName() + "ServiceImpl.java"));
        // Generate Controller
        generateFile(dataModel, "Controller.ftl", javaSrcPath.resolve("controller/" + entityDef.getName() + "Controller.java"));

        System.out.println("Generated code for entity: " + entityDef.getName());
    }

    private void generateFile(Map<String, Object> dataModel, String templateName, Path outputPath) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(templateName);
        try (Writer fileWriter = new FileWriter(outputPath.toFile())) {
            template.process(dataModel, fileWriter);
        }
        System.out.println("Generated: " + outputPath.toAbsolutePath());
    }
}