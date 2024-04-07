package edu.java.bot.service.exception;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BotExceptionHandler implements ExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onException(TelegramException e) {
        LOGGER.error("Unexpected error", e);
    }
}
