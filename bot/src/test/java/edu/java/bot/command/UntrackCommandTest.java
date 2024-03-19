package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.dict.MessageDict;
import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.link.LinkNotExistException;
import edu.java.client.scrapper.exception.ScrapperSDKException;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UntrackCommandTest extends BotApplicationTests {

    @MockBean
    private ScrapperClient scrapperSdk;
    @MockBean
    private Message message;
    @MockBean
    private User user;
    @MockBean
    private Chat chat;

    @Autowired
    private Map<String, Command> commandDict;

    @Test
    @DisplayName("Проверим чтобы url корректно парсился")
    void doCommand_url_valid() throws LinkNotExistException, ChatNotExistException  {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack https://www.victorsolkin.ru/chastye-voprosy");

        commandDict.get("untrack").doCommand(message);

        verify(scrapperSdk).untrackLink(7331L, "https://www.victorsolkin.ru/chastye-voprosy");
    }

    @Test
    @DisplayName("Проверим чтобы alias корректно парсился")
    void doCommand_alias_valid() throws LinkNotExistException, ChatNotExistException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack lol");

        commandDict.get("untrack").doCommand(message);

        verify(scrapperSdk).untrackLink(7331L, "lol");
    }

    public static Arguments[] invalidText() {
        return new Arguments[] {
            Arguments.of("/lol lol"),
            Arguments.of("untrack lol lol"),
            Arguments.of("/untrack l o l lol"),
            Arguments.of("dawdwa /untrack lol"),
            Arguments.of("/untrack"),
        };
    }

    @ParameterizedTest
    @MethodSource("invalidText")
    @DisplayName("Проверка на невалидных данных")
    void doCommand_invalid(String text) {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn(text);

        assertThat((String) commandDict.get("untrack").doCommand(message).getParameters().get("text"))
            .contains(commandDict.get("untrack").getUsage());
    }

    public static Arguments[] exceptions() {
        return new Arguments[] {
            Arguments.of(ChatNotExistException.class, MessageDict.USER_NOT_EXIST.msg),
            Arguments.of(LinkNotExistException.class, MessageDict.LINK_NOT_FOUND.msg)
        };
    }

    @ParameterizedTest
    @MethodSource("exceptions")
    @DisplayName("Проверим, что мы правильно обрабатываем исключения от scrapper")
    void doCommand_exception(Class<? extends ScrapperSDKException> exceptionClass, String expectToContain)
            throws ClientException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack lol");
        doThrow(exceptionClass).when(scrapperSdk).untrackLink(anyLong(), anyString());


        String answer = (String) commandDict.get("untrack").doCommand(message).getParameters().get("text");

        assertThat(answer).contains(expectToContain);
    }
}
