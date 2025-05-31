package com.example.cligenerator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing project configuration and templates
 */
@Service
public class ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Project configuration model
     */
    public static class ProjectConfiguration {
        private String templateVersion = "1.0.0";
        private Map<String, String> customTemplates = new HashMap<>();
        private Map<String, Object> globalVariables = new HashMap<>();
        private Map<String, String> defaultValues = new HashMap<>();

        // Constructors, getters, and setters
        public ProjectConfiguration() {
            initializeDefaults();
        }

        private void initializeDefaults() {
            defaultValues.put("javaVersion", "17");
            defaultValues.put("springBootVersion", "3.1.5");
            defaultValues.put("includeTests", "true");
            defaultValues.put("includeDocker", "true");
            defaultValues.put("includeOpenApi", "true");

            globalVariables.put("author", "Spring Boot API Generator");
            globalVariables.put("generatedTimestamp", java.time.LocalDateTime.now().toString());
        }

        // Getters and Setters
        public String getTemplateVersion() {
            return templateVersion;
        }

        public void setTemplateVersion(String templateVersion) {
            this.templateVersion = templateVersion;
        }

        public Map<String, String> getCustomTemplates() {
            return customTemplates;
        }

        public void setCustomTemplates(Map<String, String> customTemplates) {
            this.customTemplates = customTemplates;
        }

        public Map<String, Object> getGlobalVariables() {
            return globalVariables;
        }

        public void setGlobalVariables(Map<String, Object> globalVariables) {
            this.globalVariables = globalVariables;
        }

        public Map<String, String> getDefaultValues() {
            return defaultValues;
        }

        public void setDefaultValues(Map<String, String> defaultValues) {
            this.defaultValues = defaultValues;
        }
    }

    /**
     * Load project configuration from file
     */
    public ProjectConfiguration loadConfiguration(Path configPath) {
        try {
            if (Files.exists(configPath)) {
                logger.info("Loading configuration from: {}", configPath);
                String configContent = Files.readString(configPath);
                return objectMapper.readValue(configContent, ProjectConfiguration.class);
            } else {
                logger.info("Configuration file not found, using default configuration");
                return new ProjectConfiguration();
            }
        } catch (IOException e) {
            logger.warn("Failed to load configuration file, using defaults: {}", e.getMessage());
            return new ProjectConfiguration();
        }
    }

    /**
     * Save project configuration to file
     */
    public void saveConfiguration(ProjectConfiguration config, Path configPath) throws IOException {
        try {
            Files.createDirectories(configPath.getParent());
            String configJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            Files.writeString(configPath, configJson);
            logger.info("Configuration saved to: {}", configPath);
        } catch (IOException e) {
            logger.error("Failed to save configuration: {}", e.getMessage());
            throw new IOException("Failed to save configuration file", e);
        }
    }

    /**
     * Get default configuration path
     */
    public Path getDefaultConfigPath() {
        return Paths.get(System.getProperty("user.home"), ".spring-boot-generator", "config.json");
    }

    /**
     * Create configuration with custom template directory
     */
    public ProjectConfiguration createConfigurationWithCustomTemplates(Path templateDirectory)
            throws IOException {

        ProjectConfiguration config = new ProjectConfiguration();

        if (Files.exists(templateDirectory) && Files.isDirectory(templateDirectory)) {
            logger.info("Scanning custom templates in: {}", templateDirectory);

            Files.walk(templateDirectory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".ftl"))
                    .forEach(templatePath -> {
                        String templateName = templatePath.getFileName().toString()
                                .replace(".ftl", "").toLowerCase();
                        config.getCustomTemplates().put(templateName, templatePath.toString());
                        logger.debug("Found custom template: {} -> {}", templateName, templatePath);
                    });
        }

        return config;
    }

    /**
     * Validate configuration
     */
    public boolean validateConfiguration(ProjectConfiguration config) {
        if (config == null) {
            logger.error("Configuration is null");
            return false;
        }

        try {
            // Validate template paths exist
            for (Map.Entry<String, String> template : config.getCustomTemplates().entrySet()) {
                Path templatePath = Paths.get(template.getValue());
                if (!Files.exists(templatePath)) {
                    logger.warn("Custom template file not found: {}", templatePath);
                }
            }

            // Validate required default values
            String[] requiredDefaults = { "javaVersion", "springBootVersion" };
            for (String required : requiredDefaults) {
                if (!config.getDefaultValues().containsKey(required)) {
                    logger.error("Missing required default value: {}", required);
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            logger.error("Configuration validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Merge configuration with runtime parameters
     */
    public Map<String, Object> mergeConfigurationData(ProjectConfiguration config,
            Map<String, Object> runtimeData) {
        Map<String, Object> mergedData = new HashMap<>();

        // Add global variables from configuration
        mergedData.putAll(config.getGlobalVariables());

        // Add runtime data (overrides global variables if conflicts)
        mergedData.putAll(runtimeData);

        // Add default values for missing runtime data
        for (Map.Entry<String, String> defaultEntry : config.getDefaultValues().entrySet()) {
            mergedData.putIfAbsent(defaultEntry.getKey(), defaultEntry.getValue());
        }

        return mergedData;
    }
}
