package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class LinkNotExistException extends ClientException {
    public LinkNotExistException(String message) {
        super(message);
    }
}
