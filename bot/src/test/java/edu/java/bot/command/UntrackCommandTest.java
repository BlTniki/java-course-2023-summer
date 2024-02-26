package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.dict.MessageDict;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.LinkNotExistException;
import edu.java.scrapperSdk.exception.ScrapperSDKException;
import edu.java.scrapperSdk.exception.UserNotExistException;
import java.util.Map;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.client.scrapper.ScrapperSdk;
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
    private ScrapperSdk scrapperSdk;
    @MockBean
    private Message message;
    @MockBean
    private User user;
    @MockBean
    private Chat chat;

    @Autowired
    private Map<String, Command> commandDict;

    @Test
    @DisplayName("Проверим чтобы аргумент корректно парсился")
    void doCommand_valid() throws LinkNotExistException, UserNotExistException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack lol");

        commandDict.get("untrack").doCommand(message);

        verify(scrapperSdk).untrackUrl(1337L, "lol");
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
            Arguments.of(UserNotExistException.class, MessageDict.USER_NOT_EXIST.msg),
            Arguments.of(LinkNotExistException.class, MessageDict.LINK_NOT_FOUND.msg)
        };
    }

    @ParameterizedTest
    @MethodSource("exceptions")
    @DisplayName("Проверим, что мы правильно обрабатываем исключения от scrapper")
    void doCommand_exception(Class<? extends ScrapperSDKException> exceptionClass, String expectToContain) throws ScrapperSDKException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack lol");
        doThrow(exceptionClass).when(scrapperSdk).untrackUrl(anyLong(), anyString());


        String answer = (String) commandDict.get("untrack").doCommand(message).getParameters().get("text");

        assertThat(answer).contains(expectToContain);
    }
}
