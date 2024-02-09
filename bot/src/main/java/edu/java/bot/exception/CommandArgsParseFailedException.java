package edu.java.bot.exception;

import edu.java.bot.dict.CommandDict;

public class CommandArgsParseFailedException extends RuntimeException {
    public final CommandDict command;

    public CommandArgsParseFailedException(String message, CommandDict command) {
        super(message);
        this.command = command;
    }
}
