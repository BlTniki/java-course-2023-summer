package edu.java.client.bot;

import edu.java.client.bot.model.LinkUpdate;
import edu.java.client.exception.ClientException;
import jakarta.validation.constraints.NotEmpty;

public interface BotClient {
    /**
     * Отсылает обновление ссылки подписантам.
     * @param linkUpdate айди обновление ссылки
     */
    void sendLinkUpdate(@NotEmpty LinkUpdate linkUpdate) throws ClientException;
}
