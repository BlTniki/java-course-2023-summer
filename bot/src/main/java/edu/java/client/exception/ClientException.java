package edu.java.client.exception;

public class ClientException extends RuntimeException {
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
