package edu.java.client.scrapper.exception.link;

import edu.java.client.scrapper.exception.ScrapperClientException;

public class LinkNotExistException extends ScrapperClientException {
    public LinkNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
