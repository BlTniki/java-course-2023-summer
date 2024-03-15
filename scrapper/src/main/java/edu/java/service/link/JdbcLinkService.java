package edu.java.service.link;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dao.link.JdbcLinkDao;
import edu.java.domain.dao.subscription.JdbcSubscriptionDao;
import edu.java.domain.dto.LinkDto;
import edu.java.domain.dto.SubscriptionDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import edu.java.service.exception.EntityValidationFailedException;
import edu.java.service.link.model.Link;
import java.util.List;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JdbcLinkService implements LinkService {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LINK_NOT_EXIST_UNEXPECTED =
        "Failed to find a link with id %d, but there is a subscription with id %d that relates to it";
    private final JdbcChatDao chatDao;
    private final JdbcLinkDao linkDao;
    private final JdbcSubscriptionDao subscriptionDao;

    public JdbcLinkService(JdbcChatDao chatDao, JdbcLinkDao linkDao, JdbcSubscriptionDao subscriptionDao) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.subscriptionDao = subscriptionDao;
    }

    private void validateChatId(long chatId) throws EntityNotFoundException {
        chatDao.findById(chatId).orElseThrow(() -> new EntityNotFoundException(
            "Chat with id %d not exist".formatted(chatId), ErrorCode.TG_CHAT_NOT_FOUND
        ));
    }

    private Link convertToLink(SubscriptionDto subscription) {
        return linkDao.findById(subscription.linkId())
            .map(linkDto -> new Link(subscription.id(), linkDto.url(), subscription.alias()))
            .orElseThrow(handleUnexpectedEmptyResult(subscription));
    }

    @NotNull private static Supplier<RuntimeException> handleUnexpectedEmptyResult(SubscriptionDto subscriptionDto) {
        return () -> {
            String errorMessage = String.format(
                LINK_NOT_EXIST_UNEXPECTED,
                subscriptionDto.linkId(), subscriptionDto.id()
            );
            LOGGER.error(errorMessage);
            return new RuntimeException(errorMessage);
        };
    }

    @Override
    public List<Link> getByChatId(long chatId) throws EntityNotFoundException {
        validateChatId(chatId);
        return subscriptionDao.findByChatId(chatId).stream()
            .map(this::convertToLink)
            .toList();
    }


    @Override
    public Link trackLink(long chatId, AddLinkRequest addLinkRequest)
        throws EntityNotFoundException, EntityAlreadyExistException, EntityValidationFailedException {
        validateChatId(chatId);

        return null;
    }

    @Override
    public Link untrackLink(long chatId, RemoveLinkRequest removeLinkRequest)
        throws EntityNotFoundException, EntityValidationFailedException {
        validateChatId(chatId);

        SubscriptionDto subscriptionDto = subscriptionDao.findByChatIdAndAlias(chatId, removeLinkRequest.alias())
            .orElseThrow(() -> new EntityNotFoundException(
                "Link not found for alias: " + removeLinkRequest.alias(), ErrorCode.ALIAS_NOT_FOUND
            ));
        LinkDto linkDto = linkDao.findById(subscriptionDto.linkId()).orElseThrow(
            handleUnexpectedEmptyResult(subscriptionDto)
        );

        // removing subscription
        subscriptionDao.remove(subscriptionDto.id());

        // deleting a link if there are no subscriptions to it
        if (subscriptionDao.findByLinkId(linkDto.id()).isEmpty()) {
            linkDao.remove(linkDto.id());
        }

        return new Link(subscriptionDto.id(), linkDto.url(), subscriptionDto.alias());
    }
}
