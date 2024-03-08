package edu.java.bot.command;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import edu.java.bot.dict.MessageDict;
import edu.java.bot.utils.SendMessageUtils;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.exception.UserAlreadyExistException;

public class StartCommand implements Command {
    private final ScrapperSdk scrapperSdk;
    private final Message message;

    public StartCommand(ScrapperSdk scrapperSdk, Message message) {
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
