package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.client.scrapper.ScrapperClient;
import edu.java.client.scrapper.exception.chat.ChatAlreadyExistException;

public class StartCommand implements Command {
    private static final String NAME = "start";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Регистрирует новый чат";

    private final ScrapperClient scrapperClient;

    public StartCommand(ScrapperClient scrapperClient) {
        this.scrapperClient = scrapperClient;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        try {
            scrapperClient.registerChat(message.chat().id());
        } catch (ChatAlreadyExistException e) {
            return SendRequestUtils.buildMessageMarkdown(message, MessageDict.USER_ALREADY_SIGN_UP.msg);
        }
        return SendRequestUtils.buildMessageMarkdown(message, MessageDict.SUCCESSFUL_SIGN_UP.msg);
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
