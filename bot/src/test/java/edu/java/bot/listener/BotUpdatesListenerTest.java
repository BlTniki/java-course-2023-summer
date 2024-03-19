package edu.java.bot.listener;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.BotApplicationTests;
import edu.java.bot.controller.listener.BotUpdatesListener;
import edu.java.bot.service.UpdatesService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BotUpdatesListenerTest extends BotApplicationTests {
    @TestConfiguration
    static class BotUpdatesListenerTestConfig {
        @Bean
        public BotUpdatesListener botUpdatesListener(UpdatesService updatesService) {
            return new BotUpdatesListener(updatesService);
        }
    }

    @Autowired
    private BotUpdatesListener botUpdatesListener;
    @MockBean
    private UpdatesService updatesService;

    @Test
    @DisplayName("Проверим чтобы все апдейты были переданы в сервис")
    void process() {
        List<Update> updateList = List.of(
            mock(Update.class),
            mock(Update.class),
            mock(Update.class)
        );

        var answer = botUpdatesListener.process(updateList);

        assertThat(answer).isEqualTo(UpdatesListener.CONFIRMED_UPDATES_ALL);
        verify(updatesService, times(3)).processUpdate(any());
    }
}
