package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.BotSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UpdatesServiceImpl implements UpdatesService {
    private final BotSender botSender;

    public UpdatesServiceImpl(BotSender botSender) {
        this.botSender = botSender;
    }


    @Override
    public void processUpdate(@NotNull Update update) {
        long chatId = update.message().chat().id();
        botSender.send(new SendMessage(chatId, "Hello! " + update.message().text()));
    }
}
