package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class UrlAlreadyExistException extends ClientException {
    public UrlAlreadyExistException(String message) {
        super(message);
    }
}
