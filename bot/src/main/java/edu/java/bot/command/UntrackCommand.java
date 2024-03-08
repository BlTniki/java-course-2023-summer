package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UntrackCommand implements Command {
    private static final Pattern UNTRACK_ARGUMENTS = Pattern.compile("^/untrack\\s(\\S+)$");

    private final ScrapperSdk scrapperSdk;
    private final Message message;

    private final String alias;

    public UntrackCommand(ScrapperSdk scrapperSdk, Message message) {
        this.scrapperSdk = scrapperSdk;
        this.message = message;

        Matcher matcher = UNTRACK_ARGUMENTS.matcher(message.text());
        if (!matcher.matches()) {
            if (!matcher.matches()) {
                throw new CommandArgsParseFailedException(
                    "Bad /untrack agrs: " + message.text(), CommandDict.UNTRACK
                );
            }
        }
        this.alias = matcher.group(1);
    }

    @Override
    public AbstractSendRequest<?> doCommand() {
        scrapperSdk.untrackUrl(message.from().id(), alias);
        return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_UNTRACK.msg);
    }
}
