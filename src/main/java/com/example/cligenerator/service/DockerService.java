package com.example.cligenerator.service;

import com.example.cligenerator.model.DatabaseConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating Docker configuration files
 */
@Service
public class DockerService {

    private final TemplateService templateService;

    public DockerService(TemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Generate Docker configuration for the project
     */
    public void generateDockerConfiguration(Path projectPath, String artifactId, String javaVersion,
            DatabaseConfig databaseConfig) throws IOException {

        generateDockerfile(projectPath, artifactId, javaVersion);
        generateDockerCompose(projectPath, artifactId, databaseConfig);
        generateDockerIgnore(projectPath);
    }

    private void generateDockerfile(Path projectPath, String artifactId, String javaVersion) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("artifactId", artifactId);
        data.put("javaVersion", javaVersion);

        Path dockerfilePath = projectPath.resolve("Dockerfile");

        String dockerfileContent = String.format("""
                FROM openjdk:%s-jre-slim

                VOLUME /tmp

                COPY target/%s-*.jar app.jar

                EXPOSE 8080

                ENTRYPOINT ["java","-jar","/app.jar"]
                """, javaVersion, artifactId);

        Files.writeString(dockerfilePath, dockerfileContent);
    }

    private void generateDockerCompose(Path projectPath, String artifactId, DatabaseConfig databaseConfig)
            throws IOException {

        StringBuilder composeContent = new StringBuilder();
        composeContent.append("version: '3.8'\n\n");
        composeContent.append("services:\n");
        composeContent.append("  app:\n");
        composeContent.append("    build: .\n");
        composeContent.append("    ports:\n");
        composeContent.append("      - \"8080:8080\"\n");
        composeContent.append("    environment:\n");

        if (databaseConfig.getType() != DatabaseConfig.DatabaseType.H2) {
            composeContent.append("      - SPRING_DATASOURCE_URL=").append(databaseConfig.getUrl()).append("\n");
            composeContent.append("      - SPRING_DATASOURCE_USERNAME=").append(databaseConfig.getUsername())
                    .append("\n");
            composeContent.append("      - SPRING_DATASOURCE_PASSWORD=").append(databaseConfig.getPassword())
                    .append("\n");
            composeContent.append("    depends_on:\n");
            composeContent.append("      - database\n\n");

            generateDatabaseService(composeContent, databaseConfig);
        }

        composeContent.append("\nvolumes:\n");
        composeContent.append("  db_data:\n");

        Path composePath = projectPath.resolve("docker-compose.yml");
        Files.writeString(composePath, composeContent.toString());
    }

    private void generateDatabaseService(StringBuilder composeContent, DatabaseConfig databaseConfig) {
        composeContent.append("  database:\n");

        switch (databaseConfig.getType()) {
            case MYSQL:
                composeContent.append("    image: mysql:8.0\n");
                composeContent.append("    environment:\n");
                composeContent.append("      - MYSQL_ROOT_PASSWORD=rootpassword\n");
                composeContent.append("      - MYSQL_DATABASE=").append(extractDatabaseName(databaseConfig.getUrl()))
                        .append("\n");
                composeContent.append("      - MYSQL_USER=").append(databaseConfig.getUsername()).append("\n");
                composeContent.append("      - MYSQL_PASSWORD=").append(databaseConfig.getPassword()).append("\n");
                composeContent.append("    ports:\n");
                composeContent.append("      - \"3306:3306\"\n");
                break;

            case POSTGRESQL:
                composeContent.append("    image: postgres:15\n");
                composeContent.append("    environment:\n");
                composeContent.append("      - POSTGRES_DB=").append(extractDatabaseName(databaseConfig.getUrl()))
                        .append("\n");
                composeContent.append("      - POSTGRES_USER=").append(databaseConfig.getUsername()).append("\n");
                composeContent.append("      - POSTGRES_PASSWORD=").append(databaseConfig.getPassword()).append("\n");
                composeContent.append("    ports:\n");
                composeContent.append("      - \"5432:5432\"\n");
                break;

            case MARIADB:
                composeContent.append("    image: mariadb:10.9\n");
                composeContent.append("    environment:\n");
                composeContent.append("      - MARIADB_ROOT_PASSWORD=rootpassword\n");
                composeContent.append("      - MARIADB_DATABASE=").append(extractDatabaseName(databaseConfig.getUrl()))
                        .append("\n");
                composeContent.append("      - MARIADB_USER=").append(databaseConfig.getUsername()).append("\n");
                composeContent.append("      - MARIADB_PASSWORD=").append(databaseConfig.getPassword()).append("\n");
                composeContent.append("    ports:\n");
                composeContent.append("      - \"3306:3306\"\n");
                break;
        }

        composeContent.append("    volumes:\n");
        composeContent.append("      - db_data:/var/lib/mysql\n");
    }

    private void generateDockerIgnore(Path projectPath) throws IOException {
        String dockerIgnoreContent = """
                target/
                !target/*.jar
                .mvn/
                mvnw
                mvnw.cmd
                *.md
                .git/
                .gitignore
                Dockerfile
                docker-compose.yml
                """;

        Path dockerIgnorePath = projectPath.resolve(".dockerignore");
        Files.writeString(dockerIgnorePath, dockerIgnoreContent);
    }

    private String extractDatabaseName(String url) {
        // Simple extraction of database name from JDBC URL
        if (url.contains("/")) {
            String[] parts = url.split("/");
            String lastPart = parts[parts.length - 1];
            return lastPart.split("\\?")[0]; // Remove query parameters
        }
        return "mydb";
    }
}
