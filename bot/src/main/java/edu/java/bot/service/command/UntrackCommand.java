package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.exception.link.LinkNotExistException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UntrackCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NAME = "untrack";
    private static final String USAGE = "<url> <alias(optional)>";
    private static final String DESCRIPTION = "Начать отслеживать новый url";
    private static final Pattern UNTRACK_ARGUMENTS = Pattern.compile("^/untrack\\s(\\S+)$");

    private final ScrapperClient scrapperClient;

    public UntrackCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        Matcher matcher = UNTRACK_ARGUMENTS.matcher(message.text());
        if (!matcher.matches()) {
            return SendRequestUtils.buildMessageMarkdown(
                message,
                MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                    getName(), getUsage()
                )
            );
        }

        var alias = matcher.group(1);

        SendMessage sendMessage;
        try {
            scrapperClient.untrackLink(message.chat().id(), alias);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.SUCCESSFUL_UNTRACK.msg);

        } catch (ChatNotExistException e) {
            LOGGER.warn(e);
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_NOT_EXIST.msg);
        } catch (LinkNotExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.LINK_NOT_FOUND.msg);
        }

        return sendMessage;
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
