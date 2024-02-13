package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.BotApplicationTests;
import edu.java.bot.dict.CommandDict;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class HelpCommandTest extends BotApplicationTests {
    @MockBean
    private Message message;
    @MockBean
    private Chat chat;

    @Test
    @DisplayName("Проверим чтобы выводились все команды и передавался правильный id")
    void doCommand() {
        when(chat.id()).thenReturn(7331L);
        when(message.chat()).thenReturn(chat);
        Command command = new Command.Help(message);

        SendMessage response = (SendMessage) command.doCommand();
        String text = (String) response.getParameters().get("text");
        Long id = (Long) response.getParameters().get("chat_id");

        Arrays.stream(CommandDict.values())
            .forEach(c -> assertThat(text).contains(c.name));

        assertThat(id).isEqualTo(7331L);
    }
}
