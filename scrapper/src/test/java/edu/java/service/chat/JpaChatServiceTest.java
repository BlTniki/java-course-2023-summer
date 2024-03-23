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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaChatServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @Bean
        public JpaChatService jpaChatService(JpaChatDao chatDao) {
            return new JpaChatService(chatDao);
        }
    }
    @MockBean
    public JpaChatDao chatDao;
    @Autowired
    private JpaChatService jpaChatService;

    @Test
    void addChat_whenChatDoesNotExist_thenChatShouldBeAdded() {
        // given
        var expected = new JpaChatDto(1L);
        when(chatDao.findById(expected.getId())).thenReturn(Optional.empty());
        when(chatDao.save(any())).thenReturn(expected);

        // when
        jpaChatService.addChat(expected.getId());

        // then
        verify(chatDao).save(any());
    }

    @Test
    void addChat_whenChatAlreadyExist_thenThrowEntityAlreadyExistException() {
        // given
        long id = 1L;
        when(chatDao.findById(id)).thenReturn(Optional.of(new JpaChatDto(id)));

        // then
        assertThatThrownBy(() -> jpaChatService.addChat(id))
            .isInstanceOf(EntityAlreadyExistException.class)
            .hasMessageContaining("Chat with id 1 already exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_ALREADY_EXIST);
    }

    @Test
    void removeChat_whenChatExist_thenChatShouldBeRemoved() {
        // given
        long id = 1L;
        when(chatDao.findById(id)).thenReturn(Optional.of(new JpaChatDto(id)));

        // when
        jpaChatService.removeChat(id);

        // then
        verify(chatDao).deleteById(id);
    }

    @Test
    void removeChat_whenChatDoesNotExist_thenThrowEntityNotFoundException() {
        // given
        long id = 1L;
        when(chatDao.findById(id)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> jpaChatService.removeChat(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Chat with id 1 not exist")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TG_CHAT_NOT_FOUND);
    }
}
