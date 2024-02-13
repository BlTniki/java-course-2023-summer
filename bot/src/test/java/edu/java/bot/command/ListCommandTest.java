package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.dict.MessageDict;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.model.Link;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListCommandTest extends BotApplicationTests {
    @MockBean
    private ScrapperSdk scrapperSdk;
    @MockBean
    private Message message;
    @MockBean
    private User user;
    @MockBean
    private Chat chat;

    @Test
    @DisplayName("Проверим, что бот выводит все url")
    void doCommand_some_links() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(scrapperSdk.getAllUserTracks(anyLong())).thenReturn(List.of(
            new Link("link1", "alias1", null),
            new Link("link2", "alias2", null),
            new Link("link3", "alias3", null)
        ));

        String answer = (String) new Command.List(scrapperSdk, message).doCommand().getParameters().get("text");

        verify(scrapperSdk).getAllUserTracks(1337L);
        assertThat(answer).contains(List.of("link1", "link2", "link3"));
    }

    @Test
    @DisplayName("Проверим, что бот выводит отдельное сообщение если url нет")
    void doCommand() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(scrapperSdk.getAllUserTracks(anyLong())).thenReturn(List.of());

        String answer = (String) new Command.List(scrapperSdk, message).doCommand().getParameters().get("text");

        verify(scrapperSdk).getAllUserTracks(1337L);
        assertThat(answer).isEqualTo(MessageDict.LINK_LIST_EMPTY.msg);
    }
}
