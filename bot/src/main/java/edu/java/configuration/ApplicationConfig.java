package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commandParser.CommandParser;
import edu.java.bot.dict.CommandDict;
import edu.java.bot.exception.BotExceptionHandler;
import edu.java.bot.listener.BotUpdatesListener;
import edu.java.bot.sender.BotSender;
import edu.java.bot.service.UpdatesService;
import edu.java.bot.service.UpdatesServiceImpl;
import edu.java.client.scrapper.ScrapperClient;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    int threadsPerExecutor
) {
    @Bean
    public Logger logger() {
        return LogManager.getLogger();
    }

    @Bean
    public TelegramBot telegramBot() {
        var bot = new TelegramBot(telegramToken);
        bot.execute(new SetMyCommands(
            Arrays.stream(CommandDict.values()).map(CommandDict::toBotCommand).toList().toArray(new BotCommand[0])
        ));
        return bot;
    }

    @Bean
    @Scope("prototype")
    public Executor executor() {
        return Executors.newFixedThreadPool(threadsPerExecutor);
    }

    @Bean
    public BotExceptionHandler botExceptionHandler(Logger logger) {
        return new BotExceptionHandler(logger);
    }

    @Bean
    public BotSender botSender(Logger logger, TelegramBot bot, Executor executor) {
        return new BotSender(logger, bot, executor);
    }

    @Bean
    public CommandParser commandParser(ScrapperClient scrapperSdk) {
        return new CommandParser(scrapperSdk);
    }

    @Bean
    public UpdatesService updatesService(Logger logger, BotSender botSender, CommandParser commandParser) {
        return new UpdatesServiceImpl(logger, botSender, commandParser);
    }

    @Bean
    public BotUpdatesListener botUpdatesListener(
            TelegramBot bot,
            BotExceptionHandler botExceptionHandler,
            UpdatesService updatesService,
            Executor executor
        ) {
        var botUpdatesListener =  new BotUpdatesListener(updatesService, executor);
        bot.setUpdatesListener(botUpdatesListener, botExceptionHandler);
        return botUpdatesListener;
    }
}
