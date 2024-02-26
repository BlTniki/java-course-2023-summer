package edu.java.bot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.BotApplicationTests;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BotSenderTest extends BotApplicationTests {
    @TestConfiguration
    static class BotSenderTestConfig {
        @Bean
        public BotSender botSender(Logger logger, TelegramBot telegramBot, Executor executor) {
            return new BotSender(logger, telegramBot, executor);
        }
    }
    @Autowired
    private BotSender botSender;
    @MockBean
    private TelegramBot bot;

    @Test
    @DisplayName("Проверим, что мы отправляем боту сообщения")
    void send() {
        var sendResponse = mock(SendResponse.class);
        when(sendResponse.isOk()).thenReturn(true);
        when(bot.execute(any())).thenReturn(sendResponse);

        SendMessage sendMessage = new SendMessage(1337, "lolkek");

        botSender.send(sendMessage);

        // дождёмся выполнения задачи
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        verify(bot).execute(any());
    }

}
