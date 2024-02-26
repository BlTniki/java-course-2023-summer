package edu.java.client.scrapper.exception.link;

import edu.java.client.scrapper.exception.ScrapperClientException;

public class LinkAlreadyExistException extends ScrapperClientException {
    public LinkAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
