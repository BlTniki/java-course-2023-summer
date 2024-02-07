package edu.java.bot.command;

import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * Объект, реализующий функционал команды бота.
 */
public sealed interface Command {
    /**
     * Выполняет функционал команды и генерирует ответ для данного chatId.
     * @param chatId для которого требуется сгенерировать ответ.
     * @return ответ, который можно отправить через {@link edu.java.bot.sender.BotSender}.
     */
    AbstractSendRequest<?> doCommand(long chatId);

    final class PLaceHolder implements Command {
        @Override
        public AbstractSendRequest<?> doCommand(long chatId) {
            return new SendMessage(chatId, "Not implemented, lol");
        }
    }
}
