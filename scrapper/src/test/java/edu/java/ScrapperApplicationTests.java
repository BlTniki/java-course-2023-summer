package edu.java;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.ClientConfig;
import edu.java.configuration.IntegrationTest;
import edu.java.controller.LinksController;
import edu.java.controller.TgChatController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@WireMockTest
@EnableConfigurationProperties({ApplicationConfig.class, ClientConfig.class})
@ActiveProfiles("test")
public class ScrapperApplicationTests extends IntegrationTest {
    public static WireMockServer wireMockServer;

    @MockBean
    public LinksController linksController;
    @MockBean
    public TgChatController tgChatController;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(8085);
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void contextLoads() {
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        String dbName = jdbcTemplate.queryForObject("SELECT current_database()", String.class);
        assertThat("scrapper").isEqualTo(dbName);
    }
}
