package edu.java;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.ClientConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@WireMockTest
@EnableConfigurationProperties({ApplicationConfig.class, ClientConfig.class})
@ActiveProfiles("test")
public class ScrapperApplicationTests {
    public static WireMockServer wireMockServer;

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
}
