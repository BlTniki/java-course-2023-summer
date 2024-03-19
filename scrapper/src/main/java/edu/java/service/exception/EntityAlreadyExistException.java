package edu.java.service.exception;

import edu.java.controller.model.ErrorCode;

public class EntityAlreadyExistException extends BadRequestException {
    public EntityAlreadyExistException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
