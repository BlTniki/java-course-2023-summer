package edu.java.client.scrapper.exception;

import edu.java.client.exception.ClientException;

public class LinkNotExistException extends ScrapperClientException {
    public LinkNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
