package edu.java.domain.chat.service;

import edu.java.controller.model.ErrorCode;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.chat.dto.ChatDto;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.link.service.LinkService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JdbcChatService implements ChatService {
    private final JdbcChatDao chatDao;
    private final LinkService linkService;

    public JdbcChatService(JdbcChatDao chatDao, LinkService linkService) {
        this.chatDao = chatDao;
        this.linkService = linkService;
    }

    @Override
    public void addChat(long id) throws EntityAlreadyExistException {
        if (chatDao.findById(id).isPresent()) {
            throw new EntityAlreadyExistException(
                "Chat with id %d already exist".formatted(id),
                ErrorCode.TG_CHAT_ALREADY_EXIST
            );
        }
        chatDao.add(new ChatDto(id));
    }

    @Override
    public void removeChat(long id) throws EntityNotFoundException {
        // remove any subscriptions
        // chat existing will be checked in linkService
        linkService.getByChatId(id).stream()
            .map(link -> new RemoveLinkRequest(link.alias()))
            .forEach(request -> linkService.untrackLink(id, request));

        chatDao.remove(id);
    }
}
