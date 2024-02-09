package edu.java.bot.utils;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

public final class SendMessageUtils {
    private SendMessageUtils() {
    }

    /**
     * Создаёт SendMessage, работающий с Markdown разметкой.
     * @param message сообщение адресата
     * @param m2Message тест сообщений
     * @return SendMessage, работающий с Markdown разметкой.
     */
    public static SendMessage buildM(Message message, String m2Message) {
        return new SendMessage(message.chat().id(), m2Message).parseMode(ParseMode.Markdown);
    }
}
