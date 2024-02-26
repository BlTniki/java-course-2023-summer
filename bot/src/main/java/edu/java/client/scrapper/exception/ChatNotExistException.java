package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class ChatNotExistException extends ScrapperClientException {
    public ChatNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
