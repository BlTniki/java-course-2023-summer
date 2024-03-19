package edu.java.client.exception;

public class ForbiddenClientException extends ClientException {
    public ForbiddenClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
