package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.bot.dict.MessageDict;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.model.LinkResponse;
import edu.java.client.scrapper.model.ListLinksResponse;
import java.net.URI;
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
    private ScrapperClient scrapperClient;
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
        when(scrapperClient.getAllUserTracks(anyLong())).thenReturn(
            new ListLinksResponse(
                List.of(
                    new LinkResponse(1, URI.create("link1"), "alias1"),
                    new LinkResponse(2, URI.create("link2"), "alias2"),
                    new LinkResponse(3, URI.create("link3"), "alias3")
                ),
                3
            )
        );

        String answer = (String) new Command.List(scrapperClient, message).doCommand().getParameters().get("text");

        verify(scrapperClient).getAllUserTracks(7331L);
        assertThat(answer).contains(List.of("link1", "link2", "link3"));
    }

    @Test
    @DisplayName("Проверим, что бот выводит отдельное сообщение если url нет")
    void doCommand() {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        when(message.from()).thenReturn(user);
        when(scrapperClient.getAllUserTracks(anyLong())).thenReturn(new ListLinksResponse(List.of(), 0));

        String answer = (String) new Command.List(scrapperClient, message).doCommand().getParameters().get("text");

        verify(scrapperClient).getAllUserTracks(7331L);
        assertThat(answer).isEqualTo(MessageDict.LINK_LIST_EMPTY.msg);
    }
}
