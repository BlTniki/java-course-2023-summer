package edu.java.controller;

import edu.java.controller.filter.RateFilter;
import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.ErrorResponse;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.exception.ServiceException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionControllerHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String ERROR_DES = "error";

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> entityNotFound(EntityNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                ERROR_DES,
                e.getErrorCode(),
                EntityNotFoundException.class.getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
        ));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> badRequest(ServiceException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                ERROR_DES,
                e.getErrorCode(),
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> noResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(
                ERROR_DES,
                ErrorCode.BAD_REQUEST,
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                ERROR_DES,
                ErrorCode.BAD_REQUEST,
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> typeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                ERROR_DES,
                ErrorCode.BAD_REQUEST,
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(RateFilter.TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> tooManyRequestsException(RateFilter.TooManyRequestsException e) {
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body(new ErrorResponse(
                ERROR_DES,
                ErrorCode.TOO_MANY_REQUESTS,
                e.getClass().getName(),
                "The request limit has been exceeded. Try again after %d seconds".formatted(e.waitForRefillInSeconds),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internalException(Exception e) {
        LOGGER.error(e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                ERROR_DES,
                ErrorCode.INTERNAL_SERVER_ERROR,
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }
}
