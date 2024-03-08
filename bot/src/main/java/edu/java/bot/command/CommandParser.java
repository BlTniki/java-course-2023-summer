package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.BadMessageException;
import edu.java.bot.exception.CommandParseFailedException;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * Определяет какую команду следует выполнить на основе сообщения.
 */
public class CommandParser {
    private static final Pattern PATTERN = Pattern.compile("^/(\\w+)( .*)*$");
    private final ScrapperSdk scrapperSdk;

    public CommandParser(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
    }

    /**
     * Возвращает команду по полю name.
     * @param command name команды.
     * @return команда.
     */
    public static CommandDict byName(@NotNull String command) {
        return Arrays.stream(CommandDict.values())
            .filter(c -> c.name.equals(command))
            .findFirst()
            .orElse(null);
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

        CommandDict commandName = byName(matcher.group(1));

        return getCommand(message, commandName);
    }

    @NotNull
    @SuppressWarnings("RedundantLabeledSwitchRuleCodeBlock")
    private Command getCommand(@NotNull Message message, CommandDict commandName) {
        Command command;
        switch (commandName) {
            case START -> {
                command = new StartCommand(scrapperSdk, message);
            }
            case HELP -> {
                command = new HelpCommand(message);
            }
            case TRACK -> {
                command = new TrackCommand(scrapperSdk, message);
            }
            case UNTRACK -> {
                command = new UntrackCommand(scrapperSdk, message);
            }
            case LIST -> {
                command = new ListCommand(scrapperSdk, message);
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
