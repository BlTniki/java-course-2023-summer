package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.controller.listener.BotUpdatesListener;
import edu.java.bot.controller.sender.BotSender;
import edu.java.bot.service.UpdatesService;
import edu.java.bot.service.UpdatesServiceImpl;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.CommandParser;
import edu.java.bot.service.command.HelpCommand;
import edu.java.bot.service.command.ListCommand;
import edu.java.bot.service.command.StartCommand;
import edu.java.bot.service.command.TrackCommand;
import edu.java.bot.service.command.UntrackCommand;
import edu.java.bot.service.exception.BotExceptionHandler;
import edu.java.client.scrapper.ScrapperClient;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    int threadsPerExecutor
) {
    @Bean
    public List<Command> commands(ScrapperClient scrapperClient) {
        var commandList = new ArrayList<Command>();

        commandList.add(new StartCommand(scrapperClient));
        commandList.add(new HelpCommand(commandList));
        commandList.add(new TrackCommand(scrapperClient));
        commandList.add(new UntrackCommand(scrapperClient));
        commandList.add(new ListCommand(scrapperClient));

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
    public BotExceptionHandler botExceptionHandler() {
        return new BotExceptionHandler();
    }

    @Bean
    public BotSender botSender(TelegramBot bot) {
        return new BotSender(bot);
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
            UpdatesService updatesService
        ) {
        var botUpdatesListener =  new BotUpdatesListener(updatesService);
        bot.setUpdatesListener(botUpdatesListener, botExceptionHandler);
        return botUpdatesListener;
    }
}
