package edu.java.bot.command;

import com.pengrad.telegrambot.request.AbstractSendRequest;

/**
 * Класс, реализующий функционал команды бота.
 */
public interface Command {
    /**
     * Выполняет функционал команды и генерирует ответ для данного chatId.
     * @return ответ, который можно отправить через {@link edu.java.bot.sender.BotSender}.
     */
    AbstractSendRequest<?> doCommand();
}
