package edu.java.bot.utils;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

/**
 * Этот util класс служит для предоставления инструментов по сборке Send* классов.
 */
public final class SendRequestUtils {
    private SendRequestUtils() {
    }

    /**
     * Создаёт SendMessage, работающий с Markdown разметкой.
     * @param message сообщение адресата
     * @param textInMarkdownFormat тест сообщений
     * @return SendMessage, работающий с Markdown разметкой.
     */
    public static SendMessage buildMessageMarkdown(Message message, String textInMarkdownFormat) {
        return new SendMessage(message.chat().id(), textInMarkdownFormat).parseMode(ParseMode.Markdown);
    }
}
