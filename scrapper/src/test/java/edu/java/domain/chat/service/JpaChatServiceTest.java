package edu.java.domain.chat.service;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.chat.dao.JpaChatDao;
import edu.java.domain.chat.dto.JpaChatEntity;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.link.service.LinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class JpaChatServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @Bean
        public JpaChatService jpaChatService(JpaChatDao chatDao, LinkService linkService) {
            return new JpaChatService(chatDao, linkService);
        }
    }

    @MockBean
    private LinkService linkService;
    @Autowired
    private JpaChatDao chatDao;
    @Autowired
    private JpaChatService jpaChatService;

    @Test
    @Transactional
    @Rollback
    void addChat_whenChatDoesNotExist_thenChatShouldBeAdded() {
        // given
        var expected = new JpaChatEntity(1L);

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
        when(linkService.getByChatId(id)).thenReturn(List.of());

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
        when(linkService.getByChatId(id)).thenThrow(
            new EntityNotFoundException("Chat with id 1 not exist", ErrorCode.TG_CHAT_NOT_FOUND)
        );

        // then
        assertThatThrownBy(() -> jpaChatService.removeChat(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id 1 not exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_NOT_FOUND);
    }
}
