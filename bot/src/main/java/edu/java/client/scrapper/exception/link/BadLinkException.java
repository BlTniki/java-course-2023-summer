package edu.java.client.scrapper.exception.link;

import edu.java.client.scrapper.exception.ScrapperClientException;

public class BadLinkException extends ScrapperClientException {
    public BadLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
