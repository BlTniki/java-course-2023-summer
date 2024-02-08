package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.commandParser.CommandParser;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.exception.BadMessageException;
import edu.java.bot.sender.BotSender;
import edu.java.bot.utils.SendMessageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 * Реализует {@link UpdatesService}
 */
@Service
public class UpdatesServiceImpl implements UpdatesService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BotSender botSender;
    private final CommandParser commandParser;

    public UpdatesServiceImpl(BotSender botSender, CommandParser commandParser) {
        this.botSender = botSender;
        this.commandParser = commandParser;
    }

    @Override
    public void processUpdate(@NotNull Update update) {
        long chatId = update.message().chat().id();

        try {
            AbstractSendRequest<?> sendRequest = commandParser.parse(update.message()).doCommand();
            botSender.send(sendRequest);
        } catch (BadMessageException e) {
            LOGGER.warn(e);
            botSender.send(SendMessageUtils.buildM(chatId, e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(e);
            botSender.send(SendMessageUtils.buildM(chatId, MessageDict.INTERNAL_SERVER_ERROR.msg));
        }
    }
}
