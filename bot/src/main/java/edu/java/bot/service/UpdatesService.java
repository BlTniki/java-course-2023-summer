package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.controller.sender.BotSender;
import org.jetbrains.annotations.NotNull;

/**
 * Обрабатывает {@link Update} от Telegram API, генерирует ответ
 * и передаёт его {@link BotSender}.
 */
public interface UpdatesService {
    /**
     * Обрабатывает {@link Update} от Telegram API, генерирует ответ
     * и передаёт его {@link BotSender}.
     * @param update от Telegram API.
     */
    void processUpdate(@NotNull Update update);

    /**
     * Обрабатывает обновление ссылки от Scrapper.
     * @param linkUpdate обновление ссылки.
     */
    void processLinkUpdate(@NotNull LinkUpdate linkUpdate);
}
