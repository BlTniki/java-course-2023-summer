package edu.java.service.link.stackoverflow;

import edu.java.client.exception.ClientException;
import edu.java.client.exception.ForbiddenClientException;
import edu.java.client.exception.ResourceNotFoundClientException;
import edu.java.client.stackoverflow.StackOverflowClient;
import edu.java.client.stackoverflow.model.QuestionResponse;
import edu.java.controller.model.ErrorCode;
import edu.java.service.exception.CorruptedDataException;
import edu.java.service.exception.EntityValidationFailedException;
import edu.java.service.exception.ServiceException;
import edu.java.service.link.LinkChecker;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StackOverflowLinkChecker implements LinkChecker {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String RESOURCE_KEY = "resource";
    private static final String ID_KEY = "id";
    private static final String LAST_ACTIVITY_KEY = "last_activity";

    private final StackOverflowClient stackOverflowClient;

    public StackOverflowLinkChecker(StackOverflowClient stackOverflowClient) {
        this.stackOverflowClient = stackOverflowClient;
    }

    private Map<String, String> checkQuestion(Map<String, String> trackedData) {
        Map<String, String> newData = new HashMap<>();
        long id;
        try {
            id = Long.parseLong(trackedData.getOrDefault(ID_KEY, null));
        } catch (NumberFormatException e) {
            throw new CorruptedDataException(
                "Bad question id: " + trackedData.getOrDefault(ID_KEY, null),
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }

        QuestionResponse questionResponse;
        try {
            questionResponse = stackOverflowClient.fetchQuestions(List.of(id)).items().stream()
                .filter(q -> q.questionId() == id)
                .findAny()
                .orElseThrow(() -> new ClientException(
                    "There is no question with id=%d in stackoverflow answer".formatted(id)
                ));
        } catch (ForbiddenClientException | ResourceNotFoundClientException e) {
            throw new EntityValidationFailedException(
                "Given question id is not exist: " + id,
                ErrorCode.BAD_REQUEST
            );
        } catch (ClientException e) {
            LOGGER.error("Unexpected error");
            LOGGER.error(e);
            throw new ServiceException(
                "Unexpected error while fetching from StackOverflow",
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }

        OffsetDateTime oldLastActivityDate = OffsetDateTime.parse(trackedData.getOrDefault(
            LAST_ACTIVITY_KEY,
            "1970-01-01T00:00Z"
        ));

        if (questionResponse.lastActivityDate().isAfter(oldLastActivityDate)) {
            newData.put(LAST_ACTIVITY_KEY, questionResponse.lastActivityDate().toString());
        }

        return newData;
    }

    @Override
    public Map<String, String> check(Map<String, String> trackedData) throws CorruptedDataException {
        ResourceType resourceType;
        try {
            resourceType = ResourceType.valueOf(trackedData.getOrDefault(RESOURCE_KEY, null));
        } catch (IllegalArgumentException e) {
            throw new CorruptedDataException(
                "Bad resource type: " + trackedData.getOrDefault(RESOURCE_KEY, null),
                ErrorCode.INTERNAL_SERVER_ERROR
            );
        }

        if (resourceType.equals(ResourceType.QUESTIONS)) {
            return checkQuestion(trackedData);
        }

        throw new UnsupportedOperationException("Unsupported resource: " + resourceType);
    }

    @Override
    public String toUpdateMessage(Map<String, String> newData) {
        return "Обновление в вопросе";
    }
}
