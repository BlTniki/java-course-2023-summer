package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.NotNull;

/**
 * Обрабатывает {@link Update} от Telegram API, генерирует ответ
 * и передаёт его {@link edu.java.bot.sender.BotSender}.
 */
public interface UpdatesService {
    /**
     * Обрабатывает {@link Update} от Telegram API, генерирует ответ
     * и передаёт его {@link edu.java.bot.sender.BotSender}.
     * @param update от Telegram API.
     */
    void processUpdate(@NotNull Update update);
}
