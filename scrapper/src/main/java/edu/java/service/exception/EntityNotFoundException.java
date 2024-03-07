package edu.java.service.exception;

import edu.java.controller.model.ErrorCode;

public class EntityNotFoundException extends BadRequestException {
    public EntityNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
