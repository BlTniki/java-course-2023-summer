package edu.java.bot.exception;

public class CommandParseFailedException extends BadMessageException {
    public CommandParseFailedException(String message) {
        super(message);
    }
}
