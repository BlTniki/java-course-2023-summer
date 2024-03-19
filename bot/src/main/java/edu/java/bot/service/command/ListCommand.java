package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.model.ListLinksResponse;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NAME = "list";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Выводит список всех отслеживаемых url";

    private final ScrapperClient scrapperClient;

    public ListCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        ListLinksResponse links;
        try {
            links = scrapperClient.getAllUserTracks(message.chat().id());
        } catch (ChatNotExistException e) {
            LOGGER.warn(e);
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_NOT_EXIST.msg);
        }

        if (links.links().isEmpty()) {
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.LINK_LIST_EMPTY.msg);
        }

        String linksMessage = links.links().stream()
            .map(link -> MessageDict.LINK_LIST_FORMAT.msg.formatted(link.alias(), link.link()))
            .collect(Collectors.joining("\n"));

        return SendRequestUtils.buildMessageMarkdown(
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
