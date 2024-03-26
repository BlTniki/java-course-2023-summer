package edu.java.domain.chat.service;

import edu.java.controller.model.ErrorCode;
import edu.java.domain.chat.dao.JpaChatDao;
import edu.java.domain.chat.dto.JpaChatEntity;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class JpaChatService implements ChatService {
    private final JpaChatDao chatDao;

    public JpaChatService(JpaChatDao chatDao) {
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
        chatDao.save(new JpaChatEntity(id));
    }

    @Override
    public void removeChat(long id) throws EntityNotFoundException {
        chatDao.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Chat with id %d not exist".formatted(id),
                ErrorCode.TG_CHAT_NOT_FOUND
            ));
        chatDao.deleteById(id);
    }
}
