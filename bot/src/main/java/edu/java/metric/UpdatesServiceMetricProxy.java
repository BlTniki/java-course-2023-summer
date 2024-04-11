package edu.java.metric;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.service.UpdatesService;
import io.micrometer.core.instrument.Counter;
import org.jetbrains.annotations.NotNull;

/**
 * Класс-прокси для сбора метрик успешной обработки сообщений
 */
public class UpdatesServiceMetricProxy implements UpdatesService {
    private final UpdatesService updatesService;
    private final Counter tgUpdatesProceededCounter;
    private final Counter tgUpdatesErrorsCounter;
    private final Counter scrapperUpdatesProceededCounter;
    private final Counter scrapperUpdatesErrorsCounter;

    public UpdatesServiceMetricProxy(
        UpdatesService updatesService,
        Counter tgUpdatesProceededCounter,
        Counter tgUpdatesErrorsCounter,
        Counter scrapperUpdatesProceededCounter,
        Counter scrapperUpdatesErrorsCounter
    ) {
        this.updatesService = updatesService;
        this.tgUpdatesProceededCounter = tgUpdatesProceededCounter;
        this.tgUpdatesErrorsCounter = tgUpdatesErrorsCounter;
        this.scrapperUpdatesProceededCounter = scrapperUpdatesProceededCounter;
        this.scrapperUpdatesErrorsCounter = scrapperUpdatesErrorsCounter;
    }

    @Override
    public void processUpdate(@NotNull Update update) {
        try {
            updatesService.processUpdate(update);
            tgUpdatesProceededCounter.increment();
        } catch (Exception e) {
            tgUpdatesErrorsCounter.increment();
            throw e;
        }
    }

    @Override
    public void processLinkUpdate(@NotNull LinkUpdate linkUpdate) {
        try {
            updatesService.processLinkUpdate(linkUpdate);
            scrapperUpdatesProceededCounter.increment();
        } catch (Exception e) {
            scrapperUpdatesErrorsCounter.increment();
            throw e;
        }
    }
}
