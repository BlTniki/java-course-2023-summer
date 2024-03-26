package edu.java.service.chat;

import edu.java.controller.model.ErrorCode;
import edu.java.domain.dao.chat.JpaChatDao;
import edu.java.domain.dto.JpaChatDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
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
        chatDao.save(new JpaChatDto(id));
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
