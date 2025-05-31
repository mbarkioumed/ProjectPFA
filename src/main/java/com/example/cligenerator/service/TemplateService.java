package com.example.cligenerator.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing custom templates
 */
@Service
public class TemplateService {

    private final Configuration freemarkerConfig;
    private final Map<String, String> customTemplates = new HashMap<>();

    public TemplateService(Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        initializeDefaultTemplates();
    }

    private void initializeDefaultTemplates() {
        customTemplates.put("entity", "Entity.ftl");
        customTemplates.put("dto", "Dto.ftl");
        customTemplates.put("repository", "Repository.ftl");
        customTemplates.put("service", "Service.ftl");
        customTemplates.put("serviceImpl", "ServiceImpl.ftl");
        customTemplates.put("controller", "Controller.ftl");
        customTemplates.put("unittest", "UnitTest.ftl");
        customTemplates.put("integrationtest", "IntegrationTest.ftl");
        customTemplates.put("dockerfile", "Dockerfile.ftl");
        customTemplates.put("openapi", "OpenApiConfig.ftl");
    }

    /**
     * Load custom templates from a directory
     */
    public void loadCustomTemplates(Path templateDirectory) throws IOException {
        if (!Files.exists(templateDirectory)) {
            return;
        }

        Files.walk(templateDirectory)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ftl"))
                .forEach(path -> {
                    String templateName = path.getFileName().toString().replace(".ftl", "").toLowerCase();
                    customTemplates.put(templateName, path.toString());
                });
    }

    /**
     * Get available template types
     */
    public Map<String, String> getAvailableTemplates() {
        return new HashMap<>(customTemplates);
    }

    /**
     * Generate code using a specific template
     */
    public void generateFromTemplate(String templateName, Map<String, Object> data, Path outputPath)
            throws IOException, TemplateException {

        String templateFile = customTemplates.get(templateName.toLowerCase());
        if (templateFile == null) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }

        Template template = freemarkerConfig.getTemplate(templateFile);

        // Ensure parent directory exists
        Files.createDirectories(outputPath.getParent());

        try (FileWriter writer = new FileWriter(outputPath.toFile())) {
            template.process(data, writer);
        }
    }

    /**
     * Check if a template exists
     */
    public boolean hasTemplate(String templateName) {
        return customTemplates.containsKey(templateName.toLowerCase());
    }

    /**
     * Process template and return the generated content as string
     */
    public String processTemplate(String templateName, Map<String, Object> data)
            throws IOException, TemplateException {

        String templateFile = customTemplates.get(templateName.toLowerCase());
        if (templateFile == null) {
            // Try direct template name if not found in custom templates
            templateFile = templateName;
        }

        Template template = freemarkerConfig.getTemplate(templateFile);

        try (java.io.StringWriter writer = new java.io.StringWriter()) {
            template.process(data, writer);
            return writer.toString();
        }
    }
}
