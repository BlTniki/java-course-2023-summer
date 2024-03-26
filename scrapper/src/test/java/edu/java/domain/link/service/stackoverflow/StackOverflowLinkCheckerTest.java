package edu.java.domain.link.service.stackoverflow;

import edu.java.ScrapperApplicationTests;
import edu.java.client.exception.ClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.model.QuestionResponse;
import edu.java.client.stackoverflow.model.QuestionsResponse;
import edu.java.controller.model.ErrorCode;
import edu.java.domain.exception.CorruptedDataException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.exception.ServiceException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StackOverflowLinkCheckerTest extends ScrapperApplicationTests {
    private final StackOverflowClient stackOverflowClient = mock(StackOverflowClient.class);
    private final StackOverflowLinkChecker stackOverflowLinkChecker = new StackOverflowLinkChecker(stackOverflowClient);

    @Test
    @DisplayName("Проверим, что мы возвращаем новые данные, если они есть")
    void checkQuestion_shouldReturnNewData_whenLastActivityDateIsAfterOldLastActivityDate() throws ClientException {
        long questionId = 123L;
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "QUESTIONS");
        trackedData.put("id", String.valueOf(questionId));
        trackedData.put("last_activity", "1970-01-01T00:00Z");

        QuestionResponse questionResponse = new QuestionResponse(questionId, 1337, OffsetDateTime.now());

        when(stackOverflowClient.fetchQuestions(ArgumentMatchers.anyList()))
            .thenReturn(new QuestionsResponse(List.of(questionResponse)));

        Map<String, String> result = stackOverflowLinkChecker.check(trackedData);

        assertThat(result).containsEntry("last_activity", questionResponse.lastActivityDate().toString());
    }

    @Test
    @DisplayName("Проверим, что мы проверяем, что id это число")
    void checkQuestion_shouldThrowCorruptedDataException_whenIdIsNotANumber() {
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "QUESTIONS");
        trackedData.put("id", "notANumber");

        assertThatThrownBy(() -> stackOverflowLinkChecker.check(trackedData))
            .isInstanceOf(CorruptedDataException.class)
            .hasMessageContaining("Bad question id: notANumber")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем неожиданные ошибки от клиента")
    void checkQuestion_shouldThrowEntityValidationFailedException_whenQuestionDoesNotExist() throws ClientException {
        long questionId = 123L;
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "QUESTIONS");
        trackedData.put("id", String.valueOf(questionId));

        when(stackOverflowClient.fetchQuestions(ArgumentMatchers.anyList()))
            .thenThrow(new ResourceNotFoundClientException("Question not found", null));

        assertThatThrownBy(() -> stackOverflowLinkChecker.check(trackedData))
            .isInstanceOf(EntityValidationFailedException.class)
            .hasMessageContaining("Given question id is not exist: " + questionId)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.URL_VALIDATION_FAILED);
    }

    @Test
    @DisplayName("Проверим, что мы отлавливаем неожиданные ошибки от клиента")
    void checkQuestion_shouldThrowServiceException_whenUnexpectedErrorOccurs() throws ClientException {
        long questionId = 123L;
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "QUESTIONS");
        trackedData.put("id", String.valueOf(questionId));

        when(stackOverflowClient.fetchQuestions(ArgumentMatchers.anyList()))
            .thenThrow(new ClientException("Unexpected error", null));

        assertThatThrownBy(() -> stackOverflowLinkChecker.check(trackedData))
            .isInstanceOf(ServiceException.class)
            .hasMessageContaining("Unexpected error while fetching from StackOverflow")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверим, что мы проверяем, что мы поддерживаем ресурс")
    void check_shouldThrowUnsupportedOperationException_whenResourceTypeIsUnsupported() {
        Map<String, String> trackedData = new HashMap<>();
        trackedData.put("resource", "UNSUPPORTED_RESOURCE");

        assertThatThrownBy(() -> stackOverflowLinkChecker.check(trackedData))
            .isInstanceOf(CorruptedDataException.class)
            .hasMessageContaining("Bad resource type: UNSUPPORTED_RESOURCE");
    }

    @Test
    @DisplayName("Проверим, что мы правильно представляем новые данные в виде сообщения")
    void toUpdateMessage_shouldReturnUpdateMessage() {
        Map<String, String> newData = new HashMap<>();
        newData.put("last_update", OffsetDateTime.now().toString());

        String updateMessage = stackOverflowLinkChecker.toUpdateMessage(newData);

        assertThat(updateMessage).isEqualTo("Обновление в вопросе");
    }
}
