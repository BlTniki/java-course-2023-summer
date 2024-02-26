package edu.java.bot.service;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.BotApplicationTests;
import edu.java.bot.command.Command;
import edu.java.bot.commandParser.CommandParser;
import edu.java.bot.sender.BotSender;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class UpdatesServiceImplTest extends BotApplicationTests {
    @TestConfiguration
    static class UpdatesServiceImplTestConfig {
        @Bean
        public UpdatesServiceImpl updatesService(BotSender botSender, CommandParser commandParser, Logger logger) {
            return new UpdatesServiceImpl(logger, botSender, commandParser);
        }
    }

    @Autowired
    private UpdatesServiceImpl updatesService;
    @MockBean
    private BotSender botSender;
    @MockBean
    CommandParser commandParser;

    @BeforeEach
    void beforeAll() {
        var command = mock(Command.Start.class);
        var sendRequest = mock(AbstractSendRequest.class);
        //noinspection unchecked
        when(command.doCommand()).thenReturn(sendRequest);
        when(commandParser.parse(any())).thenReturn(command);
    }

    @Test
    @DisplayName("Проверим, что сервис парсит сообщение и передаёт на отправку ответ")
    public void processUpdate() {
        var update = mock(Update.class);
        var message = mock(Message.class);
        var chat = mock(Chat.class);
        when(chat.id()).thenReturn(1337L);
        when(message.chat()).thenReturn(chat);
        when(update.message()).thenReturn(message);

        updatesService.processUpdate(update);

        verify(commandParser).parse(message);
        verify(botSender).send(any());
    }

    @Test
    @DisplayName("Проверим, что сервис парсит сообщение и передаёт на отправку ответ")
    public void processUpdate_null_message() {
        var update = mock(Update.class);
        when(update.message()).thenReturn(null);

        updatesService.processUpdate(update);

        verifyNoInteractions(commandParser);
        verifyNoInteractions(botSender);
    }
}
