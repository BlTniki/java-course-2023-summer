package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class LinkAlreadyExistException extends ClientException {
    public LinkAlreadyExistException(String message) {
        super(message);
    }
}