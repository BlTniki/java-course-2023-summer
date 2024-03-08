package edu.java.bot.controller.listener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.UpdatesService;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Реализует {@link UpdatesListener}. Получает коллекцию {@link Update}
 * и асинхронно передаёт в {@link UpdatesService}.
 */
public class BotUpdatesListener implements UpdatesListener {
    private final UpdatesService updatesService;

    public BotUpdatesListener(@NotNull UpdatesService updatesService) {
        this.updatesService = updatesService;
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        updates.forEach(updatesService::processUpdate);

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
