package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class LinkAlreadyExistException extends ScrapperClientException {
    public LinkAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
