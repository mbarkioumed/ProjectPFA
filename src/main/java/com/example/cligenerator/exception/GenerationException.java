package com.example.cligenerator.exception;

/**
 * Custom exception for project generation errors
 */
public class GenerationException extends Exception {
    private final String component;
    private final String details;

    public GenerationException(String component, String message, String details) {
        super(message);
        this.component = component;
        this.details = details;
    }

    public GenerationException(String component, String message, String details, Throwable cause) {
        super(message, cause);
        this.component = component;
        this.details = details;
    }

    public String getComponent() {
        return component;
    }

    public String getDetails() {
        return details;
    }
}
