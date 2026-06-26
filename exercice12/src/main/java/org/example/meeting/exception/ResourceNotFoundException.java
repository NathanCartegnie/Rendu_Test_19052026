package org.example.meeting.exception;

/**
 * Levée quand une ressource demandée est introuvable (404).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
