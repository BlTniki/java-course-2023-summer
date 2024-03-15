package edu.java.service.exception;

import edu.java.controller.model.ErrorCode;

public class CorruptedDataException extends ServiceException {
    public CorruptedDataException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
