package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import edu.java.BotApplicationTests;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.bot.service.command.Command;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.UserAlreadyExistException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StartCommandTest extends BotApplicationTests {
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
    @DisplayName("Проверим чтобы команда передавала корректный id на регистрацию")
    void doCommand() throws UserAlreadyExistException {
        when(user.id()).thenReturn(1337L);
        when(chat.id()).thenReturn(7331L);
        when(message.from()).thenReturn(user);
        when(message.chat()).thenReturn(chat);
        Command command = commandDict.get("start");

        command.doCommand(message);

        verify(scrapperSdk).registerUser(1337L);
    }
}
