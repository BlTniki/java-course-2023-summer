package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.controller.sender.BotSender;
import edu.java.bot.service.command.CommandParser;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.service.exception.BadMessageException;
import edu.java.bot.utils.SendRequestUtils;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Реализует {@link UpdatesService}
 */
public class UpdatesServiceImpl implements UpdatesService {
    public static final String LINK_UPDATE_MESSAGE = "Обновление в %s\n\n%s";
    private static final Logger LOGGER = LogManager.getLogger();
    private final BotSender botSender;
    private final CommandParser commandParser;

    public UpdatesServiceImpl(BotSender botSender, CommandParser commandParser) {
        this.botSender = botSender;
        this.commandParser = commandParser;
    }

    @Override
    public void processUpdate(@NotNull Update update) {
        if (update.message() == null) {
            LOGGER.warn("Got update with no message...");
            return;
        }
        var message = update.message();

        AbstractSendRequest<?> sendRequest;
        try {
            sendRequest = commandParser.parse(update.message()).doCommand(update.message());
        } catch (BadMessageException e) {
            LOGGER.warn(e);
            sendRequest = SendRequestUtils.buildMessageMarkdown(message, e.getMessage());

        } catch (Exception e) {
            LOGGER.error(e);
            sendRequest = SendRequestUtils.buildMessageMarkdown(message, MessageDict.INTERNAL_SERVER_ERROR.msg);
        }

        botSender.send(sendRequest);
    }

    @Override
    public void processLinkUpdate(@NotNull LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().stream().map(tgChatId -> SendRequestUtils.buildMessageMarkdown(
                tgChatId,
                MessageDict.LINK_UPDATE_MESSAGE.msg.formatted(
                    linkUpdate.link().toString(),
                    linkUpdate.description()
                )
            )).forEach(botSender::send);
    }
}
