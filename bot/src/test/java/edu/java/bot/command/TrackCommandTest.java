package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.dict.MessageDict;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.AliasAlreadyExistException;
import edu.java.scrapperSdk.exception.ScrapperSDKException;
import edu.java.scrapperSdk.exception.UrlAlreadyExistException;
import edu.java.scrapperSdk.exception.UserNotExistException;
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

class TrackCommandTest extends BotApplicationTests {
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
    @DisplayName("Проверим чтобы аргументы с alias корректно парсились")
    void doCommand_valid_with_alias()
        throws UrlAlreadyExistException, AliasAlreadyExistException, UserNotExistException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/track http://localhost:123/ dawd");

        commandDict.get("track").doCommand(message);

        verify(scrapperSdk).trackNewUrl(1337L, "http://localhost:123/", "dawd");
    }

    @Test
    @DisplayName("Проверим чтобы аргументы без alias корректно парсились")
    void doCommand_valid_no_alias() throws UrlAlreadyExistException, AliasAlreadyExistException, UserNotExistException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/track http://localhost:123/");

        commandDict.get("track").doCommand(message);

        verify(scrapperSdk).trackNewUrl(1337L, "http://localhost:123/");
    }

    public static Arguments[] invalidText() {
        return new Arguments[] {
            Arguments.of("/lol lol lol"),
            Arguments.of("track lol lol"),
            Arguments.of("/track l o l lol"),
            Arguments.of("dawdwa /track lol lol"),
            Arguments.of("/track"),
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

        assertThat((String) commandDict.get("track").doCommand(message).getParameters().get("text"))
            .contains(commandDict.get("track").getUsage());
    }

    public static Arguments[] exceptions() {
        return new Arguments[] {
            Arguments.of(UserNotExistException.class, MessageDict.USER_NOT_EXIST.msg),
            Arguments.of(UrlAlreadyExistException.class, MessageDict.URL_ALREADY_EXIST.msg),
            Arguments.of(AliasAlreadyExistException.class, MessageDict.ALIAS_ALREADY_EXIST.msg)
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
        when(message.text()).thenReturn("/track http://localhost:123/");
        doThrow(exceptionClass).when(scrapperSdk).trackNewUrl(anyLong(), anyString());


        String answer = (String) commandDict.get("track").doCommand(message).getParameters().get("text");

        assertThat(answer).contains(expectToContain);
    }
}
