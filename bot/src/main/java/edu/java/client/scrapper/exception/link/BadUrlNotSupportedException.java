package edu.java.client.scrapper.exception.link;

public class BadUrlNotSupportedException extends BadLinkException {
    public BadUrlNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
