package edu.java.configuration;

import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.chat.service.ChatService;
import edu.java.domain.chat.service.JdbcChatService;
import edu.java.domain.link.dao.JdbcLinkDao;
import edu.java.domain.link.dao.JdbcSubscriptionDao;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.service.JdbcLinkService;
import edu.java.domain.link.service.LinkChecker;
import edu.java.domain.link.service.LinkParser;
import edu.java.domain.link.service.LinkService;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public ChatService chatService(JdbcChatDao jdbcChatDao, LinkService linkService) {
        return new JdbcChatService(jdbcChatDao, linkService);
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
