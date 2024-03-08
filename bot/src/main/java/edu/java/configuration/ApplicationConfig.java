package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.command.Command;
import edu.java.bot.command.CommandParser;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.exception.BotExceptionHandler;
import edu.java.bot.listener.BotUpdatesListener;
import edu.java.bot.sender.BotSender;
import edu.java.bot.service.UpdatesService;
import edu.java.bot.service.UpdatesServiceImpl;
import edu.java.scrapperSdk.ScrapperSdk;
import edu.java.scrapperSdk.ScrapperSdkStub;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ScrapperSdk scrapperSdk() {
        return new ScrapperSdkStub();
    }

    @Bean
    public List<Command> commands(ScrapperSdk scrapperSdk) {
        var commandList = new ArrayList<Command>();

        commandList.add(new StartCommand(scrapperSdk));
        commandList.add(new HelpCommand(commandList));
        commandList.add(new TrackCommand(scrapperSdk));
        commandList.add(new UntrackCommand(scrapperSdk));
        commandList.add(new ListCommand(scrapperSdk));

        return Collections.unmodifiableList(commandList);
    }

    @Bean
    public Map<String, Command> commandDict(List<Command> commands) {
        var commandDict = new HashMap<String, Command>();
        for (var command: commands) {
            commandDict.put(command.getName(), command);
        }
        return commandDict;
    }

    @Bean
    public TelegramBot telegramBot(List<Command> commands) {
        var bot = new TelegramBot(telegramToken);
        bot.execute(new SetMyCommands(
            commands.stream().map(Command::toBotCommand).toList().toArray(new BotCommand[0])
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
    public CommandParser commandParser(Map<String, Command> commandDict) {
        return new CommandParser(commandDict);
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
