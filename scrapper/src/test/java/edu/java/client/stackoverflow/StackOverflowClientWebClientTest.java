package edu.java.client.stackoverflow;

import edu.java.ScrapperApplicationTests;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import edu.java.client.exception.ClientException;
import edu.java.client.stackoverflow.model.QuestionResponse;
import edu.java.client.stackoverflow.model.QuestionsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StackOverflowClientWebClientTest extends ScrapperApplicationTests {
    @Autowired
    StackOverflowClient stackOverflowClient;

    @Test
    @DisplayName("Проверим, что мы правильно запрашиваем вопросы")
    void fetchQuestions_success() throws ClientException {
        List<Long> questionIds= List.of(1642028L, 59535522L);
        String questionIdsJoined = questionIds.stream().map(Object::toString).collect(Collectors.joining("%3B"));
        wireMockServer.stubFor(
            get(urlPathEqualTo("/questions/" + questionIdsJoined))
                .withQueryParam("site", equalTo("stackoverflow"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("client/stackoverflow/get_questions_success.json")
                )
        );
        QuestionsResponse expected = new QuestionsResponse(List.of(
            new QuestionResponse(
                1642028L,
                26,
                OffsetDateTime.ofInstant(Instant.ofEpochSecond(1705410153), ZoneOffset.UTC)
            ),
            new QuestionResponse(
                59535522,
                3,
                OffsetDateTime.ofInstant(Instant.ofEpochSecond(1652526628), ZoneOffset.UTC)
            )
        ));

        QuestionsResponse actual = stackOverflowClient.fetchQuestions(questionIds);

        assertThat(actual)
            .isEqualTo(expected);
    }

    @Test
    @DisplayName("Проверим, что мы кидаем ошибку")
    void fetchQuestions_clientException() {
        List<Long> questionIds= List.of(1642028L, 59535522L);
        String questionIdsJoined = questionIds.stream().map(Object::toString).collect(Collectors.joining("%3B"));
        wireMockServer.stubFor(
            get(urlPathEqualTo("/questions/" + questionIdsJoined))
                .withQueryParam("site", equalTo("stackoverflow"))
                .willReturn(
                    aResponse()
                        .withStatus(418)
                )
        );

        assertThatThrownBy(() -> stackOverflowClient.fetchQuestions(questionIds))
            .isInstanceOf(ClientException.class);
    }
}
