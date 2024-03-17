package edu.java.service.link;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.client.bot.model.LinkUpdate;
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
import edu.java.service.link.model.LinkDescriptor;
import edu.java.service.link.model.ServiceType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile("prod")
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
        int count = subscriptionDao.findByChatId(chatId).size() + 1;
        // loop until find available alias
        // Will fuck me up on edge cases. But who cares?
        while (subscriptionDao.findByChatIdAndAlias(chatId, String.valueOf(count)).isPresent()) {
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
    public List<LinkUpdate> updateLinksFrom(OffsetDateTime from) {
        var linkToCheck = linkDao.findFromLastUpdate(from);
        ObjectMapper objectMapper = new ObjectMapper();
        List<LinkUpdate> result = new ArrayList<>();

        for (var linkDto : linkToCheck) {
            // check for update
            LinkDescriptor linkDescriptor;
            try {
                linkDescriptor = new LinkDescriptor(
                    ServiceType.valueOf(linkDto.serviceType()),
                    objectMapper.readValue(linkDto.trackedData(), JSON_MAP_TYPE_REF)
                );
            } catch (JsonProcessingException e) {
                LOGGER.error(e);
                continue;
            }

            LinkChecker linkChecker = linkCheckerDict.get(linkDescriptor.serviceType());
            Map<String, String> newData = linkChecker.check(linkDescriptor.trackedData());

            if (newData.isEmpty()) {
                continue;
            }

            mergeData(newData, linkDescriptor);

            // save
            try {
                linkDao.update(new LinkDto(
                    linkDto.id(),
                    linkDto.url(),
                    linkDescriptor.serviceType().name(),
                    objectMapper.writeValueAsString(linkDescriptor.trackedData()),
                    OffsetDateTime.now()
                ));
            } catch (JsonProcessingException e) {
                LOGGER.error(e);
                throw new RuntimeException(e);
            }

            // load all link subscribers
            var subscribers = subscriptionDao.findByLinkId(linkDto.id()).stream()
                .map(SubscriptionDto::chatId)
                .toList();

            result.add(new LinkUpdate(
                linkDto.id(),
                linkDto.url(),
                linkChecker.toUpdateMessage(newData),
                subscribers
            ));
        }

        return result;
    }
}
