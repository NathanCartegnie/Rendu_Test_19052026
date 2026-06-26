package org.example.meeting.exception;

/**
 * Levée quand une règle métier empêche l'action demandée (409).
 */
public class BusinessConflictException extends RuntimeException {
    public BusinessConflictException(String message) {
        super(message);
    }
}
