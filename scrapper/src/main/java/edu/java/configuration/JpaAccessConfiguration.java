package edu.java.configuration;

import edu.java.domain.dao.chat.JpaChatDao;
import edu.java.domain.dao.link.JpaLinkDao;
import edu.java.domain.dao.subscription.JpaSubscriptionDao;
import edu.java.service.chat.ChatService;
import edu.java.service.chat.JpaChatService;
import edu.java.service.link.JpaLinkService;
import edu.java.service.link.LinkChecker;
import edu.java.service.link.LinkParser;
import edu.java.service.link.LinkService;
import edu.java.service.link.model.ServiceType;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public ChatService chatService(JpaChatDao jpaChatDao) {
        return new JpaChatService(jpaChatDao);
    }

    @Bean
    public LinkService linkService(
        JpaChatDao chatDao,
        JpaLinkDao linkDao,
        JpaSubscriptionDao subscriptionDao,
        LinkParser linkParser,
        Map<ServiceType, LinkChecker> linkCheckerDict
    ) {
        return new JpaLinkService(
            chatDao,
            linkDao,
            subscriptionDao,
            linkParser,
            linkCheckerDict
        );
    }
}
