package edu.java.service.link;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.dao.link.LinkDao;
import edu.java.domain.dao.subscription.SubscriptionDao;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import edu.java.service.exception.EntityValidationFailedException;
import edu.java.service.link.model.Link;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LinkServiceImpl implements LinkService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkDao linkDao;
    private final SubscriptionDao subscriptionDao;

    public LinkServiceImpl(LinkDao linkDao, SubscriptionDao subscriptionDao) {
        this.linkDao = linkDao;
        this.subscriptionDao = subscriptionDao;
    }

    @Override
    public List<Link> getByChatId(long chatId) throws EntityNotFoundException {
        return subscriptionDao.findByChatId(chatId).stream()
            .map(subscription -> linkDao.findById(subscription.linkId())
                .map(linkDto -> new Link(subscription.id(), linkDto.url(), subscription.alias()))
                .orElseThrow(() -> {
                    LOGGER.error(
                        "Failed to find a link with id %d, but there is a subscription with id %d that relates to it"
                            .formatted(subscription.linkId(), subscription.id())
                    );
                    return new RuntimeException("Link not found for id: " + subscription.linkId());
                })
            ).toList();
    }

    @Override
    public Link trackLink(long chatId, AddLinkRequest addLinkRequest)
        throws EntityNotFoundException, EntityAlreadyExistException, EntityValidationFailedException {
        ch
    }

    @Override
    public Link untrackLink(long chatId, RemoveLinkRequest removeLinkRequest)
        throws EntityNotFoundException, EntityValidationFailedException {
        return null;
    }
}
