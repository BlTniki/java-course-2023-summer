package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class ChatNotExistException extends ClientException {
    public ChatNotExistException(String message) {
        super(message);
    }
}
