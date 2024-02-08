package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;

/**
 * Объект, реализующий функционал команды бота.
 */
public sealed interface Command {
    /**
     * Выполняет функционал команды и генерирует ответ для данного chatId.
     * @return ответ, который можно отправить через {@link edu.java.bot.sender.BotSender}.
     */
    AbstractSendRequest<?> doCommand();

    final class Start implements Command {
        private final Message message;

        public Start(Message message) {
            this.message = message;
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            return SendMessageUtils.buildM(message.from().id(), MessageDict.SUCCESSFUL_SIGN_UP.msg);
        }
    }
}
