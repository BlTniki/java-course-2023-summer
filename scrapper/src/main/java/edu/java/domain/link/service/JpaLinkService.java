package edu.java.domain.link.service;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.chat.dao.JpaChatDao;
import edu.java.domain.chat.dto.JpaChatEntity;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.link.dao.JpaLinkDao;
import edu.java.domain.link.dao.JpaSubscriptionDao;
import edu.java.domain.link.dto.JpaLinkEntity;
import edu.java.domain.link.dto.JpaSubscriptionEntity;
import edu.java.domain.link.dto.Link;
import edu.java.domain.link.dto.LinkDescriptor;
import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.dto.ServiceType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaLinkService implements LinkService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final JpaChatDao chatDao;
    private final JpaLinkDao linkDao;
    private final JpaSubscriptionDao subscriptionDao;
    private final LinkParser linkParser;
    private final Map<ServiceType, LinkChecker> linkCheckerDict;

    public JpaLinkService(
        JpaChatDao chatDao,
        JpaLinkDao linkDao,
        JpaSubscriptionDao subscriptionDao,
        LinkParser linkParser,
        Map<ServiceType, LinkChecker> linkCheckerDict
    ) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.subscriptionDao = subscriptionDao;
        this.linkParser = linkParser;
        this.linkCheckerDict = linkCheckerDict;
    }

    private Link mapToLink(JpaSubscriptionEntity subscriptionEntity) {
        return new Link(
            subscriptionEntity.getId(),
            subscriptionEntity.getLink().getUrl(),
            subscriptionEntity.getAlias()
        );
    }

    private JpaChatEntity validateChatId(long chatId) throws EntityNotFoundException {
        return chatDao.findById(chatId).orElseThrow(() -> new EntityNotFoundException(
            "Chat with id %d not exist".formatted(chatId), ErrorCode.TG_CHAT_NOT_FOUND
        ));
    }

    private String generateAlias(long chatId) {
        var existingAlias = subscriptionDao.findByChatId(chatId).stream()
            .map(JpaSubscriptionEntity::getAlias)
            .collect(Collectors.toSet());
        int count = existingAlias.size() + 1;
        while (existingAlias.contains(String.valueOf(count))) {
            count++;
        }
        return String.valueOf(count);
    }

    private static LinkDescriptor mergeData(Map<String, String> newData, LinkDescriptor linkDescriptor) {
        var newTrackedData = new HashMap<>(linkDescriptor.trackedData());
        newTrackedData.putAll(newData);
        return new LinkDescriptor(linkDescriptor.serviceType(), newTrackedData);
    }

    private @NotNull JpaLinkEntity createAndSaveLinkEntity(URI url) {
        LinkDescriptor linkDescriptor = linkParser.parse(url);

        Map<String, String> newData = linkCheckerDict.get(linkDescriptor.serviceType())
            .check(linkDescriptor.trackedData());

        linkDescriptor = mergeData(newData, linkDescriptor);

        JpaLinkEntity linkEntity = new JpaLinkEntity(
            null,
            url,
            linkDescriptor.serviceType(),
            linkDescriptor.trackedData(),
            OffsetDateTime.now()
        );
        linkEntity = linkDao.save(linkEntity);

        return linkEntity;
    }

    @Override
    public List<Link> getByChatId(long chatId) throws EntityNotFoundException {
        validateChatId(chatId);

        return subscriptionDao.findByChatId(chatId).stream()
            .map(this::mapToLink)
            .toList();
    }

    @Override
    public Link trackLink(long chatId, AddLinkRequest addLinkRequest)
            throws EntityNotFoundException, EntityAlreadyExistException, EntityValidationFailedException {
        JpaChatEntity chatEntity = validateChatId(chatId);

        URI url = addLinkRequest.link();
        String alias = addLinkRequest.alias();

        // validate request
        if (alias != null && subscriptionDao.findByChatIdAndAlias(chatId, addLinkRequest.alias()).isPresent()) {
            throw new EntityAlreadyExistException(
                "Alias is already in use by you: " + addLinkRequest.alias(), ErrorCode.ALIAS_ALREADY_EXIST
            );
        }
        if (alias == null) {
            alias = generateAlias(chatId);
        }

        JpaLinkEntity linkEntity = linkDao.findByUrl(url).orElseGet(() -> createAndSaveLinkEntity(url));

        JpaSubscriptionEntity subscriptionEntity = new JpaSubscriptionEntity(
            null,
            chatEntity,
            linkEntity,
            alias
        );
        subscriptionEntity = subscriptionDao.save(subscriptionEntity);

        return mapToLink(subscriptionEntity);
    }

    @Override
    public Link untrackLink(long chatId, RemoveLinkRequest removeLinkRequest)
            throws EntityNotFoundException, EntityValidationFailedException {
        validateChatId(chatId);

        var subscriptionEntity = subscriptionDao.findByChatIdAndAlias(chatId, removeLinkRequest.alias())
            .orElseThrow(() -> new EntityNotFoundException(
                "Link not found for alias: " + removeLinkRequest.alias(), ErrorCode.ALIAS_NOT_FOUND
            ));
        // removing subscription
        subscriptionDao.deleteById(subscriptionEntity.getId());

        // deleting a link if there are no subscriptions to it
        if (subscriptionDao.findByLinkId(subscriptionEntity.getLink().getId()).isEmpty()) {
            linkDao.deleteById(subscriptionEntity.getLink().getId());
        }

        return mapToLink(subscriptionEntity);
    }

    @Override
    public List<LinkUpdateDto> updateLinksFrom(OffsetDateTime from) {
        return linkDao.findFromLastCheck(from).parallelStream()
            .peek(linkEntity -> LOGGER.info("Checking: " + linkEntity.getUrl()))
            .map(this::updateLink)
            .filter(Objects::nonNull)
            .toList();
    }

    @Nullable private LinkUpdateDto updateLink(JpaLinkEntity linkEntity) {
        // check for update
        LinkDescriptor linkDescriptor = new LinkDescriptor(
            linkEntity.getServiceType(),
            linkEntity.getTrackedData()
        );

        LinkChecker linkChecker = linkCheckerDict.get(linkDescriptor.serviceType());
        Map<String, String> newData = linkChecker.check(linkDescriptor.trackedData());

        linkDescriptor = mergeData(newData, linkDescriptor);

        // save
        linkEntity.setTrackedData(linkDescriptor.trackedData());
        linkEntity.setLastCheck(OffsetDateTime.now());
        linkDao.save(linkEntity);

        // if there are no new data don't return update
        if (newData.isEmpty()) {
            return null;
        }

        // load all link subscribers
        var subscribers = subscriptionDao.findByLinkId(linkEntity.getId()).stream()
            .map(subscriptionEntity -> subscriptionEntity.getChat().getId())
            .toList();

        return new LinkUpdateDto(
            linkEntity.getId(),
            linkEntity.getUrl(),
            linkChecker.toUpdateMessage(newData),
            subscribers
        );
    }
}
