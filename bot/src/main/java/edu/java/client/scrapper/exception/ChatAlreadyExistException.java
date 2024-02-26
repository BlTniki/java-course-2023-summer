package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class ChatAlreadyExistException extends ClientException {
    public ChatAlreadyExistException(String message) {
        super(message);
    }
}
