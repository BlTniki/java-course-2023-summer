package edu.java.service.link;

import edu.java.controller.model.ErrorCode;
import edu.java.service.exception.ParseFailedException;
import edu.java.service.link.model.LinkDescriptor;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

/**
 * Парсер url для определения:
 * Какой сервис следует отслеживать? (GitHub, StackOverflow и т.д.)
 * Какой ресурс сервиса следует отслеживать? (repository, issue, question и т.д.)
 * Реализуется в виде цепочки обязанностей под каждый сервис
 */
public abstract class LinkParser {
    protected LinkParser nextParser;

    public abstract @NotNull LinkDescriptor parse(@NotNull URI url) throws ParseFailedException;

    public static LinkParser link(@NotNull LinkParser first, LinkParser... chain) {
        LinkParser head = first;
        for (LinkParser nextInChain : chain) {
            head.nextParser = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    protected @NotNull LinkDescriptor parseNext(@NotNull URI url) throws ParseFailedException {
        if (nextParser == null) {
            throw new ParseFailedException("This service is not supported", ErrorCode.URL_NOT_SUPPORTED);
        }
        return nextParser.parse(url);
    }
}
