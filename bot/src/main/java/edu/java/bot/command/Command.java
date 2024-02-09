package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.CommandParseFailedException;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.AliasAlreadyExistException;
import edu.java.scrapperSdk.exception.LinkNotExistException;
import edu.java.scrapperSdk.exception.UrlAlreadyExistException;
import edu.java.scrapperSdk.exception.UserAlreadyExistException;
import edu.java.scrapperSdk.exception.UserNotExistException;
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
        private final ScrapperSdk scrapperSdk;
        private final Message message;

        public Start(ScrapperSdk scrapperSdk, Message message) {
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

        private final ScrapperSdk scrapperSdk;
        private final Message message;
        private final String url;
        private final String alias;

        public Track(ScrapperSdk scrapperSdk, Message message) throws CommandParseFailedException {
            this.scrapperSdk = scrapperSdk;
            this.message = message;

            Matcher matcher = TRACK_ARGUMENTS.matcher(message.text());
            if (!matcher.matches()) {
                throw new CommandParseFailedException(
                    MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                        CommandDict.TRACK.name, CommandDict.TRACK.usage
                    )
                );
            }
            this.url = matcher.group(1);
            this.alias = matcher.group(2);
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            try {
                if (alias.isEmpty()) {
                    scrapperSdk.trackNewUrl(message.from().id(), url);
                } else {
                    scrapperSdk.trackNewUrl(message.from().id(), url, alias);
                }
            } catch (UserNotExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.USER_NOT_EXIST.msg);
            } catch (UrlAlreadyExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.URL_ALREADY_EXIST.msg);
            } catch (AliasAlreadyExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.ALIAS_ALREADY_EXIST.msg);
            }

            return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_TRACK.msg);
        }
    }

    final class Untrack implements Command {
        private static final Pattern UNTRACK_ARGUMENTS = Pattern.compile("^/untrack\\s(\\S+)$");

        private final ScrapperSdk scrapperSdk;
        private final Message message;

        private final String alias;

        public Untrack(ScrapperSdk scrapperSdk, Message message) throws CommandParseFailedException {
            this.scrapperSdk = scrapperSdk;
            this.message = message;

            Matcher matcher = UNTRACK_ARGUMENTS.matcher(message.text());
            if (!matcher.matches()) {
                throw new CommandParseFailedException(
                    MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                        CommandDict.UNTRACK.name, CommandDict.UNTRACK.usage
                    )
                );
            }
            this.alias = matcher.group(1);
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            try {
                scrapperSdk.untrackUrl(message.from().id(), alias);
            } catch (UserNotExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.USER_NOT_EXIST.msg);
            } catch (LinkNotExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.LINK_NOT_FOUND.msg);
            }

            return SendMessageUtils.buildM(message, MessageDict.SUCCESSFUL_UNTRACK.msg);
        }
    }

    final class List implements Command {
        private final ScrapperSdk scrapperSdk;
        private final Message message;

        public List(ScrapperSdk scrapperSdk, Message message) {
            this.scrapperSdk = scrapperSdk;
            this.message = message;
        }

        @Override
        public AbstractSendRequest<?> doCommand() {
            try {
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

            } catch (UserNotExistException e) {
                return SendMessageUtils.buildM(message, MessageDict.USER_NOT_EXIST.msg);
            }
        }
    }
}
