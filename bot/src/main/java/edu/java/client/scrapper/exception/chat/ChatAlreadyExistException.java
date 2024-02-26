package edu.java.client.scrapper.exception.chat;

import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.exception.ScrapperClientException;

public class ChatAlreadyExistException extends ScrapperClientException {
    public ChatAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
