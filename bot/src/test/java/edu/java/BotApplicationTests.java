package edu.java;

import edu.java.bot.controller.UpdatesController;
import edu.java.configuration.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableConfigurationProperties(ApplicationConfig.class)
@ActiveProfiles("test")
public class BotApplicationTests {
    @MockBean
    public UpdatesController updatesController;
    @Test
    void contextLoads() {
    }
}
