package edu.java.bot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.response.SendResponse;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.Logger;

/**
 * Класс, отвечающий за отправку AbstractSendRequest.
 * Реализует отправку в асинхронном виде
 */
public class BotSender {
    private final Logger logger;

    private final TelegramBot bot;
    private final Executor executor;

    public BotSender(Logger logger, TelegramBot bot, Executor executor) {
        this.logger = logger;
        this.bot = bot;
        this.executor = executor;
    }

    public void send(AbstractSendRequest<?> sendRequest) {
        executor.execute(() -> {
            logger.info("execute " + sendRequest);
            SendResponse sendResponse = bot.execute(sendRequest);
            if (!sendResponse.isOk()) {
                logger.error("Error From Telegram API:" + sendResponse.description());
            }
        });
    }
}
