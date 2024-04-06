package edu.java.domain.link.dto;

import java.net.URI;
import java.util.List;

public record LinkUpdateDto(
    long id,
    URI link,
    String description,
    List<Long> tgChatIds
) {}
