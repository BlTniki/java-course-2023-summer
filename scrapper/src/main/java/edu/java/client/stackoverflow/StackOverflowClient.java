package edu.java.client.stackoverflow;

import edu.java.client.exception.ClientException;
import edu.java.client.stackoverflow.model.QuestionsResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;

public interface StackOverflowClient {
    @NotNull QuestionsResponse fetchQuestions(@NotEmpty Collection<Long> questionIds) throws ClientException;
}
