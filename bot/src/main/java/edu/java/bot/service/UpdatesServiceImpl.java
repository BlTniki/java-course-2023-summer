package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.commandParser.CommandParser;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.BadMessageException;
import edu.java.bot.exception.CommandArgsParseFailedException;
import edu.java.bot.sender.BotSender;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.client.scrapper.exception.chat.ChatAlreadyExistException;
import edu.java.client.scrapper.exception.chat.ChatNotExistException;
import edu.java.client.scrapper.exception.link.AliasAlreadyExistException;
import edu.java.client.scrapper.exception.link.LinkNotExistException;
import edu.java.client.scrapper.exception.link.UrlAlreadyExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Реализует {@link UpdatesService}
 */
public class UpdatesServiceImpl implements UpdatesService {
    private final Logger logger;
    private final BotSender botSender;
    private final CommandParser commandParser;

    public UpdatesServiceImpl(Logger logger, BotSender botSender, CommandParser commandParser) {
        this.logger = logger;
        this.botSender = botSender;
        this.commandParser = commandParser;
    }

    @Override
    public void processUpdate(@NotNull Update update) {
        if (update.message() == null) {
            logger.warn("Got update with no message...");
            return;
        }
        var message = update.message();

        AbstractSendRequest<?> sendRequest;
        try {
            sendRequest = commandParser.parse(update.message()).doCommand();
        } catch (CommandArgsParseFailedException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(
                update.message(),
                MessageDict.BAD_INPUT_WRONG_COMMAND_ARGUMENTS.msg.formatted(
                    e.command.name, e.command.usage
                )
            );
        } catch (ChatNotExistException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.USER_NOT_EXIST.msg);

        } catch (ChatAlreadyExistException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.USER_ALREADY_SIGN_UP.msg);

        } catch (LinkNotExistException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.LINK_NOT_FOUND.msg);

        } catch (UrlAlreadyExistException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.URL_ALREADY_EXIST.msg);

        } catch (AliasAlreadyExistException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.ALIAS_ALREADY_EXIST.msg);

        } catch (BadMessageException e) {
            logger.warn(e);
            sendRequest = SendMessageUtils.buildM(message, e.getMessage());

        } catch (Exception e) {
            logger.error(e);
            sendRequest = SendMessageUtils.buildM(message, MessageDict.INTERNAL_SERVER_ERROR.msg);
        }

        botSender.send(sendRequest);
    }
}
