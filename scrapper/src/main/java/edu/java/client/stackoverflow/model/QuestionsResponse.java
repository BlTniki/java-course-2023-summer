package edu.java.client.stackoverflow.model;

import java.util.List;

public record QuestionsResponse(
    List<QuestionResponse> items
) {
}
