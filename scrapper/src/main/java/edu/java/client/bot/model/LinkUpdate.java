package edu.java.client.bot.model;

import edu.java.domain.link.dto.LinkUpdateDto;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
    long id,
    URI link,
    String description,
    List<Long> tgChatIds
) {
    public static @NotNull LinkUpdate fromServiceModel(@NotNull LinkUpdateDto serviceUpdate) {
        return new LinkUpdate(
            serviceUpdate.id(),
            serviceUpdate.link(),
            serviceUpdate.description(),
            serviceUpdate.tgChatIds()
        );
    }
}
