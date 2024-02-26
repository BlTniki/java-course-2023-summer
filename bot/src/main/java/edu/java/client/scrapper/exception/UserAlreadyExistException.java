package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class UserAlreadyExistException extends ClientException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
