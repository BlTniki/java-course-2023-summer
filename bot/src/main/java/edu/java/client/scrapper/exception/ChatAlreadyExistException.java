package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class ChatAlreadyExistException extends ScrapperClientException {
    public ChatAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
