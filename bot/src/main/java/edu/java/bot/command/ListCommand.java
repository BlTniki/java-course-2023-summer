package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.stream.Collectors;

public class ListCommand implements Command {
    private static final String NAME = "list";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Выводит список всех отслеживаемых url";

    private final ScrapperSdk scrapperSdk;

    public ListCommand(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        var links = scrapperSdk.getAllUserTracks(message.from().id());

        if (links.isEmpty()) {
            return SendMessageUtils.buildM(message, MessageDict.LINK_LIST_EMPTY.msg);
        }

        String linksMessage = links.stream()
            .map(link -> MessageDict.LINK_LIST_FORMAT.msg.formatted(link.alias(), link.url()))
            .collect(Collectors.joining("\n"));

        return SendMessageUtils.buildM(
            message, MessageDict.LINK_LIST_HEADER.msg + linksMessage
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
