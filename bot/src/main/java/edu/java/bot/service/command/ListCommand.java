package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.UserNotExistException;
import edu.java.scrapperSdk.model.Link;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NAME = "list";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Выводит список всех отслеживаемых url";

    private final ScrapperSdk scrapperSdk;

    public ListCommand(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        List<Link> links;
        try {
            links = scrapperSdk.getAllUserTracks(message.from().id());
        } catch (UserNotExistException e) {
            LOGGER.warn(e);
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_NOT_EXIST.msg);
        }

        if (links.isEmpty()) {
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.LINK_LIST_EMPTY.msg);
        }

        String linksMessage = links.stream()
            .map(link -> MessageDict.LINK_LIST_FORMAT.msg.formatted(link.alias(), link.url()))
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
