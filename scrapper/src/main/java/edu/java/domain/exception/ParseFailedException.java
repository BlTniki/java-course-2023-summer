package edu.java.domain.exception;

import edu.java.controller.model.ErrorCode;

public class ParseFailedException extends BadRequestException {
    public ParseFailedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
