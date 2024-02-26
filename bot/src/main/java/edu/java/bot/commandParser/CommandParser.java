package edu.java.bot.commandParser;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.command.Command;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.BadMessageException;
import edu.java.bot.exception.CommandParseFailedException;
import edu.java.client.scrapper.ScrapperClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Определяет какую команду следует выполнить на основе сообщения.
 */
public class CommandParser {
    private static final Pattern PATTERN = Pattern.compile("^/(\\w+)( .*)*$");
    private final ScrapperClient scrapperSdk;

    public CommandParser(ScrapperClient scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
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

        if (!matcher.matches()) {
            throw new CommandParseFailedException(MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted(text));
        }

        CommandDict commandName = CommandDict.byName(matcher.group(1));

        return getCommand(message, commandName);
    }

    @NotNull
    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    private Command getCommand(@NotNull Message message, CommandDict commandName) {
        Command command;
        switch (commandName) {
            case START -> {
                command = new Command.Start(scrapperSdk, message);
            }
            case HELP -> {
                command = new Command.Help(message);
            }
            case TRACK -> {
                command = new Command.Track(scrapperSdk, message);
            }
            case UNTRACK -> {
                command = new Command.Untrack(scrapperSdk, message);
            }
            case LIST -> {
                command = new Command.List(scrapperSdk, message);
            }
            case null, default -> {
                throw new CommandParseFailedException(
                    MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted(message.text())
                );
            }
        }
        return command;
    }
}
