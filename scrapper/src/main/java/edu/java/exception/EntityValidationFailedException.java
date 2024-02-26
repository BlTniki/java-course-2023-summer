package edu.java.exception;

public class EntityValidationFailedException extends BadRequestException {
    public EntityValidationFailedException(String message) {
        super(message);
    }
}
