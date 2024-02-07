package edu.java.bot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.response.SendResponse;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс, отвечающий за отправку AbstractSendRequest.
 * Реализует отправку в асинхронном виде
 */
public class BotSender {
    private static final Logger LOGGER = LogManager.getLogger();

    private final TelegramBot bot;
    private final Executor executor;

    public BotSender(TelegramBot bot, Executor executor) {
        this.bot = bot;
        this.executor = executor;
    }

    public void send(AbstractSendRequest<?> sendRequest) {
        executor.execute(() -> {
            SendResponse sendResponse = bot.execute(sendRequest);
            LOGGER.info("execute " + sendRequest);
        });
    }
}
