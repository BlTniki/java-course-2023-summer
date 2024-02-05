package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.NotNull;

public interface UpdatesService {
    void processUpdate(@NotNull Update update);
}
