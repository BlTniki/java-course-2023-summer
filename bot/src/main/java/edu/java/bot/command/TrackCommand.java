package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrackCommand implements Command {
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
            return SendMessageUtils.buildM(
                message,
                MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                    getName(), getUsage()
                )
            );
        }
        var url = matcher.group(1);
        var alias = matcher.group(2);

        if (alias.isEmpty()) {
            scrapperSdk.trackNewUrl(message.from().id(), url);
        } else {
            scrapperSdk.trackNewUrl(message.from().id(), url, alias);
        }

        return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_TRACK.msg);
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
