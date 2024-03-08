package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.stream.Collectors;

public class ListCommand implements Command {
    private final ScrapperSdk scrapperSdk;
    private final Message message;

    public ListCommand(ScrapperSdk scrapperSdk, Message message) {
        this.scrapperSdk = scrapperSdk;
        this.message = message;
    }

    @Override
    public AbstractSendRequest<?> doCommand() {
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
}
