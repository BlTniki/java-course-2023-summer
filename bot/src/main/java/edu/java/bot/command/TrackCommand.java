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

public class TrackCommand implements Command {
    private static final Pattern TRACK_ARGUMENTS = Pattern.compile("^/track\\s(\\S+)\\s?(\\S*)$");

    private final ScrapperSdk scrapperSdk;
    private final Message message;
    private final String url;
    private final String alias;

    public TrackCommand(ScrapperSdk scrapperSdk, Message message) {
        this.scrapperSdk = scrapperSdk;
        this.message = message;

        Matcher matcher = TRACK_ARGUMENTS.matcher(message.text());
        if (!matcher.matches()) {
            throw new CommandArgsParseFailedException("Bad /track agrs: " + message.text(), CommandDict.TRACK);
        }
        this.url = matcher.group(1);
        this.alias = matcher.group(2);
    }

    @Override
    public AbstractSendRequest<?> doCommand() {
        if (alias.isEmpty()) {
            scrapperSdk.trackNewUrl(message.from().id(), url);
        } else {
            scrapperSdk.trackNewUrl(message.from().id(), url, alias);
        }

        return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_TRACK.msg);
    }
}
