package edu.java.configuration;

import edu.java.bot.command.Command;
import edu.java.bot.command.CommandParser;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.scrapperSdk.ScrapperSdk;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Profile("test")
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    int threadsPerExecutor
) {
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
    public CommandParser commandParser(Map<String, Command> commandDict) {
        return new CommandParser(commandDict);
    }
}
