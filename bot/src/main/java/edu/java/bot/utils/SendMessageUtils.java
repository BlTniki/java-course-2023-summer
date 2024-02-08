package edu.java.bot.utils;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

public final class SendMessageUtils {
    private SendMessageUtils() {
    }

    /**
     * Создаёт SendMessage, работающий с Markdown разметкой.
     * @param chatId адресат
     * @param m2Message тест сообщений
     * @return SendMessage, работающий с Markdown разметкой.
     */
    public static SendMessage buildM(long chatId, String m2Message) {
        return new SendMessage(chatId, m2Message).parseMode(ParseMode.Markdown);
    }
}
