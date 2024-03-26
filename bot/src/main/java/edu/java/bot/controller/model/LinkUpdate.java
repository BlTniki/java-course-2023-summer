package edu.java.bot.controller.model;

import java.net.URI;
import java.util.List;

public record LinkUpdate(
    long id,
    URI link,
    String description,
    List<Long> tgChatIds
) {}
