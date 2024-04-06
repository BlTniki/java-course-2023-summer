package edu.java.domain.link.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.link.dao.JdbcLinkDao;
import edu.java.domain.link.dao.JdbcSubscriptionDao;
import edu.java.domain.link.dto.Link;
import edu.java.domain.link.dto.LinkDescriptor;
import edu.java.domain.link.dto.LinkDto;
import edu.java.domain.link.dto.LinkUpdateDto;
import edu.java.domain.link.dto.ServiceType;
import edu.java.domain.link.dto.SubscriptionDto;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class JdbcLinkService implements LinkService {
    public static final TypeReference<HashMap<String, String>> JSON_MAP_TYPE_REF =
            new TypeReference<>() {
            };
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LINK_NOT_EXIST_UNEXPECTED =
        "Failed to find a link with id %d, but there is a subscription with id %d that relates to it";
    private final JdbcChatDao chatDao;
    private final JdbcLinkDao linkDao;
    private final JdbcSubscriptionDao subscriptionDao;
    private final LinkParser linkParser;
    private final Map<ServiceType, LinkChecker> linkCheckerDict;

    public JdbcLinkService(
        JdbcChatDao chatDao,
        JdbcLinkDao linkDao,
        JdbcSubscriptionDao subscriptionDao,
        LinkParser linkParser,
        Map<ServiceType, LinkChecker> linkCheckerDict
    ) {
        this.chatDao = chatDao;
        this.linkDao = linkDao;
        this.subscriptionDao = subscriptionDao;
        this.linkParser = linkParser;
        this.linkCheckerDict = linkCheckerDict;
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

    private String generateAlias(long chatId) {
        var existingAlias = subscriptionDao.findByChatId(chatId).stream()
            .map(SubscriptionDto::alias)
            .collect(Collectors.toSet());
        int count = existingAlias.size() + 1;
        while (existingAlias.contains(String.valueOf(count))) {
            count++;
        }
        return String.valueOf(count);
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

    private @NotNull LinkDto createAndSaveLinkDto(URI url) {
        LinkDescriptor linkDescriptor = linkParser.parse(url);

        Map<String, String> newData = linkCheckerDict.get(linkDescriptor.serviceType())
            .check(linkDescriptor.trackedData());

        mergeData(newData, linkDescriptor);

        ObjectMapper objectMapper = new ObjectMapper();
        LinkDto linkDto;
        try {
            linkDto = new LinkDto(
                null,
                url,
                linkDescriptor.serviceType().name(),
                objectMapper.writeValueAsString(linkDescriptor.trackedData()),
                OffsetDateTime.now()
            );
        } catch (JsonProcessingException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
        linkDto = linkDao.add(linkDto);

        return linkDto;
    }

    private static void mergeData(Map<String, String> newData, LinkDescriptor linkDescriptor) {
        for (Map.Entry<String, String> entry : newData.entrySet()) {
            linkDescriptor.trackedData().put(entry.getKey(), entry.getValue());
        }
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

        LinkDto linkDto = linkDao.findByUrl(url).orElse(createAndSaveLinkDto(url));

        SubscriptionDto subscriptionDto = new SubscriptionDto(
            null,
            chatId,
            linkDto.id(),
            alias
        );
        subscriptionDto = subscriptionDao.add(subscriptionDto);

        return new Link(subscriptionDto.id(), linkDto.url(), alias);
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

    @Override
    public List<LinkUpdateDto> updateLinksFrom(OffsetDateTime from) {
        final ObjectMapper objectMapper = new ObjectMapper();

        return linkDao.findFromLastUpdate(from).stream()
            .peek(linkDto -> LOGGER.info("Checking: " + linkDto.url()))
            .map(linkDto -> updateLink(linkDto, objectMapper))
            .filter(Objects::nonNull)
            .toList();
    }

    @Nullable private LinkUpdateDto updateLink(LinkDto linkDto, ObjectMapper objectMapper) {
        // check for update
        LinkDescriptor linkDescriptor;
        try {
            linkDescriptor = new LinkDescriptor(
                ServiceType.valueOf(linkDto.serviceType()),
                objectMapper.readValue(linkDto.trackedData(), JSON_MAP_TYPE_REF)
            );
        } catch (JsonProcessingException e) {
            LOGGER.error(e);
            return null;
        }

        LinkChecker linkChecker = linkCheckerDict.get(linkDescriptor.serviceType());
        Map<String, String> newData = linkChecker.check(linkDescriptor.trackedData());

        mergeData(newData, linkDescriptor);

        // save
        LinkDto newLinkDto;
        try {
            newLinkDto = new LinkDto(
                linkDto.id(),
                linkDto.url(),
                linkDescriptor.serviceType().name(),
                objectMapper.writeValueAsString(linkDescriptor.trackedData()),
                OffsetDateTime.now()
            );
        } catch (JsonProcessingException e) {
            LOGGER.error(e);
            throw new RuntimeException(e);
        }
        linkDao.update(newLinkDto);

        // if there are no new data don't return update
        if (newData.isEmpty()) {
            return null;
        }

        // load all link subscribers
        var subscribers = subscriptionDao.findByLinkId(linkDto.id()).stream()
            .map(SubscriptionDto::chatId)
            .toList();

        return new LinkUpdateDto(
            linkDto.id(),
            linkDto.url(),
            linkChecker.toUpdateMessage(newData),
            subscribers
        );
    }
}
