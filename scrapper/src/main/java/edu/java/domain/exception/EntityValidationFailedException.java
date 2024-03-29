package edu.java.domain.exception;

import edu.java.controller.model.ErrorCode;

public class EntityValidationFailedException extends BadRequestException {
    public EntityValidationFailedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
