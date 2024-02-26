package edu.java.client.scrapper.exception;

import edu.java.client.scrapper.model.ErrorResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ErrorResponseException extends HttpClientErrorException {
    private final ErrorResponse errorResponse;

    public ErrorResponseException(HttpStatusCode statusCode, ErrorResponse errorResponse) {
        super(statusCode);
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
