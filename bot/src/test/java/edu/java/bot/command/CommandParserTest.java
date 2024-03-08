package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import edu.java.BotApplicationTests;
import edu.java.bot.dict.MessageDict;
import edu.java.scrapperSdk.ScrapperSdk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandParserTest extends BotApplicationTests {
    @SuppressWarnings("unused")
    @MockBean
    private ScrapperSdk scrapperSdk;
    @Autowired
    private CommandParser commandParser;

    public static Arguments[] validCommands() {
        return new Arguments[] {
            Arguments.of("/start", StartCommand.class),
            Arguments.of("/help", HelpCommand.class),
            Arguments.of("/track dwad awdaw", TrackCommand.class),
            Arguments.of("/untrack dwwadwa", UntrackCommand.class),
            Arguments.of("/list", ListCommand.class)
        };
    }

    public static Arguments[] invalidCommands() {
        return new Arguments[] {
            Arguments.of(null, MessageDict.BAD_INPUT_NO_TEXT.msg),
            Arguments.of("/lol", MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted("/lol")),
            Arguments.of("/trackd awdaw", MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted("/trackd awdaw")),
            Arguments.of("untrack", MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted("untrack")),
            Arguments.of("dawdaw/list", MessageDict.BAD_INPUT_UNRECOGNIZED_COMMAND.msg.formatted("dawdaw/list"))
        };
    }

    @ParameterizedTest
    @MethodSource("validCommands")
    @DisplayName("Проверим на валидном инпуте")
    void parse_valid(String text, Class<? extends Command> expectedClass) {
        var message = mock(Message.class);
        when(message.text()).thenReturn(text);

        Class<? extends Command> actualClass = commandParser.parse(message).getClass();

        assertThat(actualClass).isEqualTo(expectedClass);
    }

    @ParameterizedTest
    @MethodSource("invalidCommands")
    @DisplayName("Проверим на невалидном инпуте")
    void parse_invalid(String text, String expectedMsg) {
        var message = mock(Message.class);
        when(message.text()).thenReturn(text);

        assertThatThrownBy(() -> commandParser.parse(message).getClass())
            .hasMessage(expectedMsg);
    }
}
