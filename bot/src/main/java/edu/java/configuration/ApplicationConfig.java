package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.command.CommandDict;
import edu.java.bot.commandParser.CommandParser;
import edu.java.bot.exception.BotExceptionHandler;
import edu.java.bot.listener.BotUpdatesListener;
import edu.java.bot.sender.BotSender;
import edu.java.bot.service.UpdatesService;
import edu.java.bot.service.UpdatesServiceImpl;
import jakarta.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
    public BotExceptionHandler botExceptionHandler() {
        return new BotExceptionHandler();
    }

    @Bean
    public BotSender botSender(TelegramBot bot, Executor executor) {
        return new BotSender(bot, executor);
    }

    @Bean
    public CommandParser commandParser() {
        return new CommandParser();
    }

    @Bean
    public UpdatesService updatesService(BotSender botSender, CommandParser commandParser) {
        return new UpdatesServiceImpl(botSender, commandParser);
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
