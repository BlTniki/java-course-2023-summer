package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.client.scrapper.ScrapperClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrackCommandTest extends BotApplicationTests {
    @MockBean
    private ScrapperClient scrapperSdk;
    @MockBean
    private Message message;
    @MockBean
    private User user;
    @MockBean
    private Chat chat;

    @Test
    @DisplayName("Проверим чтобы аргументы с alias корректно парсились")
    void doCommand_valid_with_alias() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/track http://localhost:123/ dawd");

        new Command.Track(scrapperSdk, message).doCommand();

        verify(scrapperSdk).trackNewLink(7331L, "http://localhost:123/", "dawd");
    }

    @Test
    @DisplayName("Проверим чтобы аргументы без alias корректно парсились")
    void doCommand_valid_no_alias() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/track http://localhost:123/");

        new Command.Track(scrapperSdk, message).doCommand();

        verify(scrapperSdk).trackNewLink(7331L, "http://localhost:123/");
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

        assertThatThrownBy(() -> new Command.Track(scrapperSdk, message).doCommand())
            .isInstanceOf(CommandArgsParseFailedException.class);
    }
}
