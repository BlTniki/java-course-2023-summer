package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.BotApplicationTests;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class HelpCommandTest extends BotApplicationTests {
    @MockBean
    private Message message;
    @MockBean
    private Chat chat;
    @MockBean
    private ScrapperSdk scrapperSdk;

    @Autowired
    private Map<String, Command> commandDict;

    @Test
    @DisplayName("Проверим чтобы выводились все команды и передавался правильный id")
    void doCommand() {
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        Command command = commandDict.get("help");

        SendMessage response = (SendMessage) command.doCommand(message);
        String text = (String) response.getParameters().get("text");
        Long id = (Long) response.getParameters().get("chat_id");

        commandDict.values()
            .forEach(c -> assertThat(text).contains(c.getName()));

        assertThat(id).isEqualTo(7331L);
    }
}
