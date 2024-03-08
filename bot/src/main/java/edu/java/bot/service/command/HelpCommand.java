package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand implements Command {
    private static final String NAME = "help";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Перечисление и использование доступных команд";

    private final List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        return SendRequestUtils.buildMessageMarkdown(
            message,
            commands.stream()
                .map(
                    c -> MessageDict.HELP_COMMANDS_BUILDER.msg.formatted(c.getName(), c.getUsage(), c.getDescription())
                )
                .collect(Collectors.joining("\n"))
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage() {
        return USAGE;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
