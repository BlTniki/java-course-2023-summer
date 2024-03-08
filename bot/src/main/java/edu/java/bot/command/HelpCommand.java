package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    private final Message message;

    public HelpCommand(Message message) {
        this.message = message;
    }

    @Override
    public AbstractSendRequest<?> doCommand() {
        return SendMessageUtils.buildM(
            message,
            Arrays.stream(CommandDict.values())
                .map(c -> MessageDict.HELP_COMMANDS_BUILDER.msg.formatted(c.name, c.usage, c.description))
                .collect(Collectors.joining("\n"))
        );
    }
}
