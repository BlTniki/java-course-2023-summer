package edu.java.bot.controller.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, отвечающий за отправку AbstractSendRequest.
 * Реализует отправку в асинхронном виде
 */
public class BotSender {
    private static final Logger LOGGER = LogManager.getLogger();

    private final TelegramBot bot;

    public BotSender(TelegramBot bot) {
        this.bot = bot;
    }

    public void send(AbstractSendRequest<?> sendRequest) {
        LOGGER.info("execute " + sendRequest);
        SendResponse sendResponse = bot.execute(sendRequest);
        if (!sendResponse.isOk()) {
            LOGGER.error("Error From Telegram API:" + sendResponse.description());
        }
    }
}
