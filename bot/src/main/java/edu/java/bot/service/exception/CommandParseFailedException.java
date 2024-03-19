package edu.java.bot.service.exception;

public class CommandParseFailedException extends BadMessageException {
    public CommandParseFailedException(String message) {
        super(message);
    }
}
