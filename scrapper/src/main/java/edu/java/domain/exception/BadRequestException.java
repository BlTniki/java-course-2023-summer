package edu.java.domain.exception;

import edu.java.controller.model.ErrorCode;


public class BadRequestException extends ServiceException {
    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
