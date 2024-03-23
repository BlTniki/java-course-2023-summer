package edu.java.service.chat;

import edu.java.controller.model.ErrorCode;
import edu.java.domain.dao.chat.ChatDao;
import edu.java.domain.dto.ChatDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JdbcChatService implements ChatService {
    private final ChatDao chatDao;

    public JdbcChatService(ChatDao chatDao) {
        this.chatDao = chatDao;
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
        chatDao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Chat with id %d not exist".formatted(id),
                ErrorCode.TG_CHAT_NOT_FOUND
            ));
        chatDao.remove(id);
    }
}
