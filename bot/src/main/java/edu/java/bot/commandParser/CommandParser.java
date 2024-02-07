package edu.java.bot.commandParser;

import edu.java.bot.command.Command;
import edu.java.bot.exception.CommandParseFailedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Определяет команду из сообщения.
 * Реализует паттерн "Цепочка обязанностей"
 * и при реализации новой команды следует его добавлять в общую цепь
 */
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
