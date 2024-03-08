package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.java.scrapperSdk.exception.LinkNotExistException;
import edu.java.scrapperSdk.exception.UserNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UntrackCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NAME = "untrack";
    private static final String USAGE = "<url> <alias(optional)>";
    private static final String DESCRIPTION = "Начать отслеживать новый url";
    private static final Pattern UNTRACK_ARGUMENTS = Pattern.compile("^/untrack\\s(\\S+)$");

    private final ScrapperSdk scrapperSdk;

    public UntrackCommand(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        Matcher matcher = UNTRACK_ARGUMENTS.matcher(message.text());
        if (!matcher.matches()) {
            return SendMessageUtils.buildM(
                message,
                MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                    getName(), getUsage()
                )
            );
        }

        var alias = matcher.group(1);

        SendMessage sendMessage;
        try {
            scrapperSdk.untrackUrl(message.from().id(), alias);
            sendMessage = SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_UNTRACK.msg);

        } catch (UserNotExistException e) {
            LOGGER.warn(e);
            return SendMessageUtils.buildM(message, MessageDict.USER_NOT_EXIST.msg);
        } catch (LinkNotExistException e) {
            LOGGER.warn(e);
            sendMessage = SendMessageUtils.buildM(message, MessageDict.LINK_NOT_FOUND.msg);
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
