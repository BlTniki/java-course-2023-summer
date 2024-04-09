package edu.java.configuration;

import edu.java.domain.chat.dao.JpaChatDao;
import edu.java.domain.chat.service.ChatService;
import edu.java.domain.chat.service.JpaChatService;
import edu.java.domain.link.dao.JpaLinkDao;
import edu.java.domain.link.dao.JpaSubscriptionDao;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.service.JpaLinkService;
import edu.java.domain.link.service.LinkChecker;
import edu.java.domain.link.service.LinkParser;
import edu.java.domain.link.service.LinkService;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public ChatService chatService(JpaChatDao jpaChatDao, LinkService linkService) {
        return new JpaChatService(jpaChatDao, linkService);
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
