package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.UserAlreadyExistException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс, реализующий функционал команды бота.
 */
public sealed interface Command {
    /**
     * Выполняет функционал команды и генерирует ответ для данного chatId.
     * @return ответ, который можно отправить через {@link edu.java.bot.sender.BotSender}.
     */
    AbstractSendRequest<?> doCommand();

    final class Start implements Command {
        private final ScrapperClient scrapperSdk;
        private final Message message;

        public Start(ScrapperClient scrapperSdk, Message message) {
            this.scrapperSdk = scrapperSdk;
            this.message = message;
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            try {
                scrapperSdk.registerUser(message.from().id());
            } catch (UserAlreadyExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.USER_ALREADY_SIGN_UP.msg);
            }
            return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_SIGN_UP.msg);
        }
    }

    final class Help implements Command {
        private final Message message;

        public Help(Message message) {
            this.message = message;
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            return SendMessageUtils.buildM(
                message,
                Arrays.stream(CommandDict.values())
                    .map(c -> MessageDict.HELP_COMMANDS_BUILDER.msg.formatted(c.name, c.usage, c.description))
                    .collect(Collectors.joining("\n"))
            );
        }
    }

    final class Track implements Command {
        private static final Pattern TRACK_ARGUMENTS = Pattern.compile("^/track\\s(\\S+)\\s?(\\S*)$");

        private final ScrapperClient scrapperSdk;
        private final Message message;
        private final String url;
        private final String alias;

        public Track(ScrapperClient scrapperSdk, Message message) {
            this.scrapperSdk = scrapperSdk;
            this.message = message;

            Matcher matcher = TRACK_ARGUMENTS.matcher(message.text());
            if (!matcher.matches()) {
                throw new  CommandArgsParseFailedException("Bad /track agrs: " + message.text(), CommandDict.TRACK);
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

    final class Untrack implements Command {
        private static final Pattern UNTRACK_ARGUMENTS = Pattern.compile("^/untrack\\s(\\S+)$");

        private final ScrapperClient scrapperSdk;
        private final Message message;

        private final String alias;

        public Untrack(ScrapperClient scrapperSdk, Message message) {
            this.scrapperSdk = scrapperSdk;
            this.message = message;

            Matcher matcher = UNTRACK_ARGUMENTS.matcher(message.text());
            if (!matcher.matches()) {
                if (!matcher.matches()) {
                    throw new  CommandArgsParseFailedException(
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

    final class List implements Command {
        private final ScrapperClient scrapperSdk;
        private final Message message;

        public List(ScrapperClient scrapperSdk, Message message) {
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
}
