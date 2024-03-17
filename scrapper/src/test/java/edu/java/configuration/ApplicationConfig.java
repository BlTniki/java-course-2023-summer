package edu.java.configuration;

import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dao.link.JdbcLinkDao;
import edu.java.domain.dao.subscription.JdbcSubscriptionDao;
import edu.java.service.link.LinkChecker;
import edu.java.service.link.LinkParser;
import edu.java.service.link.github.GitHubLinkChecker;
import edu.java.service.link.model.ServiceType;
import edu.java.service.link.stackoverflow.StackOverflowLinkChecker;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import static org.mockito.Mockito.mock;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Profile("test")
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler
) {
    @Bean
    public JdbcChatDao chatDao() {
        return mock(JdbcChatDao.class);
    }

    @Bean
    public JdbcLinkDao linkDao() {
        return mock(JdbcLinkDao.class);
    }

    @Bean
    public JdbcSubscriptionDao subscriptionDao() {
        return mock(JdbcSubscriptionDao.class);
    }

    @Bean
    public LinkParser linkParser() {
        return mock(LinkParser.class);
    }

    @Bean
    public Map<ServiceType, LinkChecker> linkCheckerDict() {
        var linkCheckerDict = new HashMap<ServiceType, LinkChecker>();

        linkCheckerDict.put(ServiceType.GitHub, mock(GitHubLinkChecker.class));
        linkCheckerDict.put(ServiceType.StackOverflow, mock(StackOverflowLinkChecker.class));

        return linkCheckerDict;
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }
}
