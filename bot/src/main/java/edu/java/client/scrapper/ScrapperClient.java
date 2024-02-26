package edu.java.client.scrapper;

import edu.java.client.exception.ClientException;
import edu.java.client.scrapper.model.LinkResponse;
import edu.java.client.scrapper.model.ListLinksResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Класс для общения со скраппером.
 */
public interface ScrapperClient {
    void registerChat(long tgChatId) throws ClientException;

    void deleteChat(long tgChatId) throws ClientException;

    LinkResponse trackNewLink(long userTelegramId, @NotEmpty String url, @NotEmpty String alias) throws ClientException;

    LinkResponse trackNewLink(long userTelegramId, @NotEmpty String url) throws ClientException;

    void untrackLink(long tgChatId, @NotEmpty String alias) throws ClientException;

    @NotNull ListLinksResponse getAllUserTracks(long userTelegramId) throws ClientException;
}
