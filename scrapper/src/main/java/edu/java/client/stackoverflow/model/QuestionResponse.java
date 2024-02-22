package edu.java.client.stackoverflow.model;

import java.time.OffsetDateTime;

public record QuestionResponse(
    long questionId,
    OffsetDateTime lastActivityDate
) {
}
