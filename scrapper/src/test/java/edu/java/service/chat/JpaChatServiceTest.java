package edu.java.service.chat;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.dao.chat.JpaChatDao;
import edu.java.domain.dto.JpaChatDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JpaChatServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @Bean
        public JpaChatService jpaChatService(JpaChatDao chatDao) {
            return new JpaChatService(chatDao);
        }
    }

    @Autowired
    private JpaChatDao chatDao;
    @Autowired
    private JpaChatService jpaChatService;

    @Test
    @Transactional
    @Rollback
    void addChat_whenChatDoesNotExist_thenChatShouldBeAdded() {
        // given
        var expected = new JpaChatDto(1L);

        // when
        jpaChatService.addChat(expected.getId());

        // then
        assertThat(chatDao.findById(expected.getId()))
            .isPresent();
    }

    @Test
    @Transactional
    @Rollback
    void addChat_whenChatAlreadyExist_thenThrowEntityAlreadyExistException() {
        // given
        long id = 1L;
        jpaChatService.addChat(id);

        // then
        assertThatThrownBy(() -> jpaChatService.addChat(id))
            .isInstanceOf(EntityAlreadyExistException.class)
            .hasMessageContaining("Chat with id 1 already exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_ALREADY_EXIST);
    }

    @Test
    @Transactional
    @Rollback
    void removeChat_whenChatExist_thenChatShouldBeRemoved() {
        // given
        long id = 1L;
        jpaChatService.addChat(id);

        // when
        jpaChatService.removeChat(id);

        // then
        assertThat(chatDao.findById(id))
            .isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removeChat_whenChatDoesNotExist_thenThrowEntityNotFoundException() {
        // given
        long id = 1L;

        // then
        assertThatThrownBy(() -> jpaChatService.removeChat(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id 1 not exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_NOT_FOUND);
    }
}
