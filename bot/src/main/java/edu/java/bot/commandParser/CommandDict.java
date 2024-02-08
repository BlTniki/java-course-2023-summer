package edu.java.bot.commandParser;

import com.pengrad.telegrambot.model.BotCommand;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public enum CommandDict {
    START("start", "Регистрирует нового пользователя");

    private final String name;
    private final String description;

    CommandDict(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static CommandDict byName(@NotNull String command) {
        return Arrays.stream(CommandDict.values())
            .filter(c -> c.name.equals(command))
            .findFirst()
            .orElse(null);
    }

    public BotCommand toBotCommand() {
        return new BotCommand("/" + name, description);
    }
}
