package edu.java.client.exception;

public class UnauthorizedClientException extends ClientException {
    public UnauthorizedClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
