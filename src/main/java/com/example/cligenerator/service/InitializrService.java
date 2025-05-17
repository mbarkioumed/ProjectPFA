package com.example.cligenerator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class InitializrService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String INITIALIZR_URL = "https://start.spring.io/starter.zip";

    public Path downloadAndUnzipProject(
            String name, String groupId, String artifactId, String packageName,
            String javaVersion, String springBootVersion, String dependencies,
            String outputDir) throws IOException {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(INITIALIZR_URL)
                .queryParam("type", "maven-project") // Or gradle-project
                .queryParam("language", "java")
                .queryParam("platformVersion", springBootVersion) // Renamed from bootVersion
                .queryParam("packaging", "jar")
                .queryParam("jvmVersion", javaVersion) // Renamed from javaVersion
                .queryParam("groupId", groupId)
                .queryParam("artifactId", artifactId)
                .queryParam("name", name)
                .queryParam("description", "Demo project for Spring Boot")
                .queryParam("packageName", packageName)
                .queryParam("dependencies", dependencies);

        URI url = builder.build().toUri();
        System.out.println("Fetching from Initializr: " + url);

        byte[] zipBytes = restTemplate.getForObject(url, byte[].class);

        if (zipBytes == null) {
            throw new IOException("Failed to download project from Spring Initializr.");
        }

        Path projectPath = Paths.get(outputDir, artifactId);

        // Clean up existing directory
        if (Files.exists(projectPath)) {
            Files.walk(projectPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(projectPath);

        File zipFile = projectPath.resolve("project.zip").toFile();
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(zipBytes);
        }

        unzip(zipFile.toPath(), projectPath);
        Files.delete(zipFile.toPath()); // Delete the zip file after extraction

        System.out.println("Project downloaded and unzipped to: " + projectPath.toAbsolutePath());
        return projectPath;
    }

    private void unzip(Path zipFilePath, Path destDirectory) throws IOException {
        try (InputStream fis = Files.newInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                Path newPath = destDirectory.resolve(zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    Files.copy(zis, newPath);
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
    }
}