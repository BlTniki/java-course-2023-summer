package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.exception.link.AliasAlreadyExistException;
import edu.java.client.scrapper.exception.link.BadUrlException;
import edu.java.client.scrapper.exception.link.BadUrlNotSupportedException;
import edu.java.client.scrapper.exception.link.UrlAlreadyExistException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrackCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NAME = "track";
    private static final String USAGE = "<url> <alias(optional)>";
    private static final String DESCRIPTION = "Начать отслеживать новый url";
    private static final Pattern TRACK_ARGUMENTS = Pattern.compile("^/track\\s(\\S+)\\s?(\\S*)$");

    private final ScrapperClient scrapperClient;

    public TrackCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        Matcher matcher = TRACK_ARGUMENTS.matcher(message.text());
        if (!matcher.matches()) {
            return SendRequestUtils.buildMessageMarkdown(
                message,
                MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                    getName(), getUsage()
                )
            );
        }
        var url = matcher.group(1);
        var alias = matcher.group(2);

        SendMessage sendMessage;
        try {
            if (alias.isEmpty()) {
                scrapperClient.trackNewLink(message.chat().id(), url);
            } else {
                scrapperClient.trackNewLink(message.chat().id(), url, alias);
            }
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.SUCCESSFUL_TRACK.msg);
        } catch (ChatNotExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_NOT_EXIST.msg);
        } catch (UrlAlreadyExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.URL_ALREADY_EXIST.msg);
        } catch (BadUrlException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.BAD_LINK.msg);
        } catch (BadUrlNotSupportedException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.LINK_TYPE_NOT_SUPPORTED.msg);
        } catch (AliasAlreadyExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.ALIAS_ALREADY_EXIST.msg);
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
