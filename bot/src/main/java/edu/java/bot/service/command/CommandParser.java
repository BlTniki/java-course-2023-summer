package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.service.exception.BadMessageException;
import edu.java.bot.service.exception.CommandParseFailedException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Определяет какую команду следует выполнить на основе сообщения.
 */
public class CommandParser {
    private static final Pattern PATTERN = Pattern.compile("^/(\\w+)( .*)*$");
    private final Map<String, Command> commandDict;

    public CommandParser(Map<String, Command> commandDict) {
        this.commandDict = commandDict;
    }

    /**
     * Парсит команду в сообщении и возвращает {@link Command}.
     * @param message сообщение.
     * @return {@link Command} что следует выполнить.
     * @throws BadMessageException если сообщение не имеет текста или команда не распознана.
     */
    public Command parse(@NotNull Message message) {
        String text = message.text();

        if (text == null) {
            throw new BadMessageException(MessageDict.BAD_INPUT_NO_TEXT.msg);
        }

        Matcher matcher = PATTERN.matcher(text);

        if (!matcher.matches() || !commandDict.containsKey(matcher.group(1))) {
            throw new CommandParseFailedException(MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted(text));
        }

        return commandDict.get(matcher.group(1));
    }
}
