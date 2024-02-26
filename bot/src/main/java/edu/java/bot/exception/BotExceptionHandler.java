package edu.java.bot.exception;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import org.apache.logging.log4j.Logger;

public class BotExceptionHandler implements ExceptionHandler {
    private final Logger logger;

    public BotExceptionHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onException(TelegramException e) {
        logger.error(e);
    }
}
