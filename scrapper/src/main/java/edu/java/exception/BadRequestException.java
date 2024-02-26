package edu.java.exception;

import edu.java.controller.model.ErrorCode;

public class BadRequestException extends RuntimeException {
    private final ErrorCode errorCode;

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
