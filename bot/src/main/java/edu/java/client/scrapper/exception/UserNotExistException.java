package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class UserNotExistException extends ClientException {
    public UserNotExistException(String message) {
        super(message);
    }
}
