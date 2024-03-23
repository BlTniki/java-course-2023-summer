package edu.java.service.chat;

import edu.java.ScrapperApplicationTests;
import edu.java.domain.dao.chat.JdbcChatDao;
import edu.java.domain.dto.ChatDto;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcChatServiceTest extends ScrapperApplicationTests {
    @TestConfiguration
    static class Config {
        @Bean
        public JdbcChatService jdbcChatService(JdbcChatDao chatDao) {
            return new JdbcChatService(chatDao);
        }
    }
    @MockBean
    public JdbcChatDao chatDao;
    @Autowired
    private JdbcChatService jdbcChatService;

    @Test
    void addChat_ShouldAddChat_WhenChatDoesNotExist() {
        long chatId = 1L;
        when(chatDao.findById(anyLong())).thenReturn(Optional.empty());

        jdbcChatService.addChat(chatId);

        verify(chatDao).add(new ChatDto(chatId));
    }

    @Test
    void addChat_ShouldThrowEntityAlreadyExistException_WhenChatAlreadyExists() {
        long chatId = 1L;
        when(chatDao.findById(anyLong())).thenReturn(Optional.of(new ChatDto(chatId)));

        assertThatThrownBy(() -> jdbcChatService.addChat(chatId))
            .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void removeChat_ShouldRemoveChat_WhenChatExists() {
        long chatId = 1L;
        when(chatDao.findById(anyLong())).thenReturn(Optional.of(new ChatDto(chatId)));

        jdbcChatService.removeChat(chatId);

        verify(chatDao).remove(chatId);
    }

    @Test
    void removeChat_ShouldThrowEntityNotFoundException_WhenChatDoesNotExist() {
        long chatId = 1L;
        when(chatDao.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jdbcChatService.removeChat(chatId))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
