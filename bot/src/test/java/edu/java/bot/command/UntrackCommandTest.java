package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.scrapperSdk.ScrapperSdk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    @DisplayName("Проверим чтобы аргумент корректно парсился")
    void doCommand_valid() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(message.text()).thenReturn("/untrack lol");

        new Command.Untrack(scrapperSdk, message).doCommand();

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

        assertThatThrownBy(() -> new Command.Untrack(scrapperSdk, message).doCommand())
            .isInstanceOf(CommandArgsParseFailedException.class);
    }
}
