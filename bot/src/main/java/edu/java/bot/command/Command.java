package edu.java.bot.command;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import jakarta.validation.constraints.NotNull;

/**
 * Класс, реализующий функционал команды бота.
 */
public interface Command {
    /**
     * Выполняет функционал команды и генерирует ответ для данного chatId.
     * @return ответ, который можно отправить через {@link edu.java.bot.sender.BotSender}.
     */
    AbstractSendRequest<?> doCommand(@NotNull Message message);

    /**
     * Возвращает название команды.
     * Например, для команды '/start' вернётся 'start'
     * @return название команды
     */
    String getName();

    /**
     * Возвращает описание использования команды.
     * @return описание использования команды
     */
    String getUsage();

    /**
     * Возвращает описание команды.
     * @return описание команды
     */
    String getDescription();

    /**
     * Приводит команду к Telegram API формату.
     * @return Telegram API BotCommand формат.
     */
    default BotCommand toBotCommand() {
        return new BotCommand("/" + getName(), getDescription());
    }
}
