package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.AliasAlreadyExistException;
import edu.java.scrapperSdk.exception.UrlAlreadyExistException;
import edu.java.scrapperSdk.exception.UserNotExistException;
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

    private final ScrapperSdk scrapperSdk;

    public TrackCommand(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
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
                scrapperSdk.trackNewUrl(message.from().id(), url);
            } else {
                scrapperSdk.trackNewUrl(message.from().id(), url, alias);
            }
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.SUCCESSFUL_TRACK.msg);
        } catch (UserNotExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_NOT_EXIST.msg);
        } catch (UrlAlreadyExistException e) {
            LOGGER.warn(e);
            sendMessage = SendRequestUtils.buildMessageMarkdown(message, MessageDict.URL_ALREADY_EXIST.msg);
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
