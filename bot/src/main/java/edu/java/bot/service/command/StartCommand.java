package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.service.dict.MessageDict;
import edu.java.bot.utils.SendRequestUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.UserAlreadyExistException;

public class StartCommand implements Command {
    private static final String NAME = "start";
    private static final String USAGE = "";
    private static final String DESCRIPTION = "Регистрирует новый чат";

    private final ScrapperSdk scrapperSdk;

    public StartCommand(ScrapperSdk scrapperSdk) {
        this.scrapperSdk = scrapperSdk;
    }

    @Override
    public AbstractSendRequest<?> doCommand(Message message) {
        try {
            scrapperSdk.registerUser(message.from().id());
        } catch (UserAlreadyExistException e) {
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
