package edu.java.client.scrapper.exception.chat;

import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.exception.ScrapperClientException;

public class ChatNotExistException extends ScrapperClientException {
    public ChatNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
