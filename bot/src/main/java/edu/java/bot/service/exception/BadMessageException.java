package edu.java.bot.service.exception;

public class BadMessageException extends RuntimeException {
    public BadMessageException(String message) {
        super(message);
    }
}
