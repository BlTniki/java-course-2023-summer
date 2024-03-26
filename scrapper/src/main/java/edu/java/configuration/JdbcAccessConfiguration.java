package edu.java.configuration;

import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dao.link.JdbcLinkDao;
import edu.java.domain.dao.subscription.JdbcSubscriptionDao;
import edu.java.service.chat.ChatService;
import edu.java.service.chat.JdbcChatService;
import edu.java.service.link.JdbcLinkService;
import edu.java.service.link.LinkChecker;
import edu.java.service.link.LinkParser;
import edu.java.service.link.LinkService;
import edu.java.service.link.model.ServiceType;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public ChatService chatService(JdbcChatDao jdbcChatDao) {
        return new JdbcChatService(jdbcChatDao);
    }

    @Bean
    public LinkService linkService(
        JdbcChatDao chatDao,
        JdbcLinkDao linkDao,
        JdbcSubscriptionDao subscriptionDao,
        LinkParser linkParser,
        Map<ServiceType, LinkChecker> linkCheckerDict
    ) {
        return new JdbcLinkService(
            chatDao,
            linkDao,
            subscriptionDao,
            linkParser,
            linkCheckerDict
        );
    }
}
