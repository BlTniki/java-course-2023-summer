package edu.java.bot;

import edu.java.bot.exception.CommandParseFailedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandParser {
    @Nullable CommandParser nextParser;

    /**
     * Парсит текст и создаёт на его основе {@link Command}.
     * @param input текст.
     * @return команду на выполнение.
     * @throws CommandParseFailedException если определить команду не удалось.
     */
    public abstract Command parse(@NotNull String input) throws CommandParseFailedException;

    public CommandParser link(@NotNull CommandParser parser) {
        this.nextParser = parser;
        return this;
    }

    Command parseByNext(@NotNull String input) throws CommandParseFailedException {
        if (nextParser == null) {
            throw new CommandParseFailedException("Unrecognized command: " + input);
        }
        return nextParser.parse(input);
    }
}
