package edu.java.configuration;

import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.CommandParser;
import edu.java.bot.service.command.HelpCommand;
import edu.java.bot.service.command.ListCommand;
import edu.java.bot.service.command.StartCommand;
import edu.java.bot.service.command.TrackCommand;
import edu.java.bot.service.command.UntrackCommand;
import edu.java.client.scrapper.ScrapperClient;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Profile("test")
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
    public CommandParser commandParser(Map<String, Command> commandDict) {
        return new CommandParser(commandDict);
    }
}
