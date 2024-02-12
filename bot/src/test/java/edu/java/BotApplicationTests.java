package edu.java;

import edu.java.configuration.ApplicationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EnableConfigurationProperties(ApplicationConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
public class BotApplicationTests {
    @Test
    void contextLoads() {
    }
}
