package edu.java.bot.dict;

import com.pengrad.telegrambot.model.BotCommand;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

/**
 * Словарь существующих команд. Каждую новую команду следует внести в этот словарь.
 */
public enum CommandDict {
    START("start", "", "Регистрирует нового пользователя"),
    HELP("help", "", "Перечисление и использование доступных команд"),
    TRACK("track", "<url> <alias(optional)>", "Начать отслеживать новый url"),
    UNTRACK("untrack", "<alias>", "Прекратить отслеживать url");

    public final String name;
    public final String usage;
    public final String description;

    CommandDict(String name, String usage, String description) {
        this.name = name;
        this.usage = usage;
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
