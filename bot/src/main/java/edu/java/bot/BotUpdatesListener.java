package edu.java.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.service.UpdatesService;
import java.util.List;
import java.util.concurrent.Executor;
import org.jetbrains.annotations.NotNull;

public class BotUpdatesListener implements UpdatesListener {
    private final UpdatesService updatesService;
    private final Executor executor;

    public BotUpdatesListener(@NotNull UpdatesService updatesService, Executor executor) {
        this.updatesService = updatesService;
        this.executor = executor;
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        updates.forEach(update -> executor.execute(() -> updatesService.processUpdate(update)));

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
