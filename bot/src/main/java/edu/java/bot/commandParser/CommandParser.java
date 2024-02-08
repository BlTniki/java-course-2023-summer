package edu.java.bot.commandParser;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.command.Command;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.BadMessageException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.java.bot.exception.CommandParseFailedException;
import org.jetbrains.annotations.NotNull;

public class CommandParser {
    private static final Pattern PATTERN = Pattern.compile("^/(\\w+)( .*)*$");

    public CommandParser() {
    }

    public Command parse(@NotNull Message message) throws BadMessageException {
        long chatId = message.chat().id();
        String text = message.text();

        if (text == null) {
            throw new BadMessageException(MessageDict.BAD_INPUT_NO_TEXT.msg);
        }

        Matcher matcher = PATTERN.matcher(text);

        if (!matcher.matches()) {
            throw new CommandParseFailedException(MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted(text));
        }

        CommandDict commandName = CommandDict.byName(matcher.group(1));
        Command command;
        switch (commandName) {
            case START -> {
                command = new Command.Start(message);
            }
            case null, default -> {
                throw new CommandParseFailedException(MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted(text));
            }
        }

        return command;
    }
}
