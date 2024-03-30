package edu.java.bot.controller;

import edu.java.bot.controller.exception.BadRequestException;
import edu.java.bot.controller.filter.RateFilter;
import edu.java.bot.controller.model.ErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionControllerHandler {

    public static final String ERROR_DES = "error";

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequest(BadRequestException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(
                ERROR_DES,
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Bad request",
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
                String.valueOf(HttpStatus.NOT_FOUND.value()),
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
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
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
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
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
                String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()),
                e.getClass().getName(),
                "The request limit has been exceeded. Try again after %d seconds".formatted(e.waitForRefillInSeconds),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internalException(Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(
                ERROR_DES,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                e.getClass().getName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ));
    }
}
