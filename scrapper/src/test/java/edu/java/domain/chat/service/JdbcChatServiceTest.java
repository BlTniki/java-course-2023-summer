package edu.java.domain.chat.service;

import edu.java.ScrapperApplicationTests;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.chat.dao.JdbcChatDao;
import edu.java.domain.chat.dto.ChatDto;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import edu.java.domain.link.service.LinkService;
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
        public JdbcChatService jdbcChatService(JdbcChatDao chatDao, LinkService linkService) {
            return new JdbcChatService(chatDao, linkService);
        }
    }
    @MockBean
    private LinkService linkService;
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
        when(linkService.getByChatId(chatId)).thenReturn(List.of());

        jdbcChatService.removeChat(chatId);

        verify(chatDao).remove(chatId);
    }

    @Test
    void removeChat_ShouldThrowEntityNotFoundException_WhenChatDoesNotExist() {
        long chatId = 1L;
        when(linkService.getByChatId(chatId)).thenThrow(
            new EntityNotFoundException("Chat with id 1 not exist", ErrorCode.TG_CHAT_NOT_FOUND)
        );

        assertThatThrownBy(() -> jdbcChatService.removeChat(chatId))
            .isInstanceOf(EntityNotFoundException.class);
    }
}
