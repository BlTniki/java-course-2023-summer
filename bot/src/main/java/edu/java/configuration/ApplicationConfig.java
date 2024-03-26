package edu.java.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.controller.filter.RateFilter;
import edu.java.bot.controller.limiter.RateLimiterService;
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
import io.github.bucket4j.Bandwidth;
import jakarta.validation.constraints.NotEmpty;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    int threadsPerExecutor,
    RateLimiting rateLimit
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

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "rate-limit.enable", havingValue = "true")
    public RateLimiterService rateLimiterService() {
        // validate .yml
        if (rateLimit.capacity() == null) {
            throw new IllegalArgumentException("app.rate-limit.capacity: The capacity must be specified (long)");
        }
        if (rateLimit.relief() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief: The relief must be specified (relief.tokens, relief.period in ISO 8601)"
            );
        }
        if (rateLimit.relief().tokens() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.tokens: The tokens must be specified (long)"
            );
        }
        if (rateLimit.relief().period() == null) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.period: The period must be specified (String in ISO 8601)"
            );
        }
        Duration period;
        try {
            period = Duration.parse(rateLimit.relief().period());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                "app.rate-limit.relief.period: The period must match ISO 8601 format",
                e
            );
        }

        Bandwidth bandwidth = Bandwidth.builder()
            .capacity(rateLimit.capacity())
            .refillIntervally(rateLimit.relief().tokens(), period)
            .build();
        return new RateLimiterService(bandwidth);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "rate-limit.enable", havingValue = "true")
    public FilterRegistrationBean<RateFilter> rateLimitFilter(
        RateLimiterService rateLimiterService,
        HandlerExceptionResolver handlerExceptionResolver
    ) {
        FilterRegistrationBean<RateFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RateFilter(rateLimiterService, handlerExceptionResolver));
        registrationBean.setOrder(1);

        return registrationBean;
    }

    public record RateLimiting(
        Boolean enable,
        Long capacity,
        Relief relief
    ) {}

    public record Relief(
        Long tokens,
        String period // ISO 8601
    ) {}
}
