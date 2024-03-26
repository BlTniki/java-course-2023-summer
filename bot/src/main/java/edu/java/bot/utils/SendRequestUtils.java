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

    /**
     * Создаёт SendMessage, работающий с Markdown разметкой.
     * @param chatId id чата адресата
     * @param textInMarkdownFormat тест сообщений
     * @return SendMessage, работающий с Markdown разметкой.
     */
    public static SendMessage buildMessageMarkdown(Long chatId, String textInMarkdownFormat) {
        return new SendMessage(chatId, textInMarkdownFormat).parseMode(ParseMode.Markdown);
    }
}
