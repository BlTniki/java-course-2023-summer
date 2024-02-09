package edu.java.bot.commandParser;

import com.pengrad.telegrambot.model.BotCommand;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

/**
 * Словарь существующих команд. Каждую новую команду следует внести в этот словарь.
 */
public enum CommandDict {
    START("start", "Регистрирует нового пользователя");

    private final String name;
    private final String description;

    CommandDict(String name, String description) {
        this.name = name;
        this.description = description;
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
     * Приводит команду к Telegram API формату.
     * @return Telegram API BotCommand формат.
     */
    public BotCommand toBotCommand() {
        return new BotCommand("/" + name, description);
    }
}
