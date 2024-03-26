package edu.java.domain.link.service;

import edu.java.client.bot.model.LinkUpdate;
import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;
import edu.java.domain.exception.EntityValidationFailedException;
import edu.java.domain.link.dto.Link;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    /**
     * Возвращает все отслеживаемые данным чатом ссылки.
     * @param chatId id чата
     * @return Список отслеживаемых ссылок
     * @throws EntityNotFoundException если данный чат не найден
     */
    @NotNull List<Link> getByChatId(long chatId) throws EntityNotFoundException;

    /**
     * Добавляет ссылку на отслеживание для данного чата.
     * @param chatId id чата
     * @param addLinkRequest форма для добавления
     * @return добавленная ссылка
     * @throws EntityNotFoundException если данный чат не найден
     * @throws EntityAlreadyExistException если данная ссылка уже добавлена
     * @throws EntityValidationFailedException если форма заполнена не верно или данная ссылка не поддерживается
     */
    @NotNull Link trackLink(long chatId, @NotNull AddLinkRequest addLinkRequest)
        throws EntityNotFoundException, EntityAlreadyExistException, EntityValidationFailedException;

    /**
     * Убирает ссылку из отслеживаемых для данного чата.
     * @param chatId id чата
     * @param removeLinkRequest форма для удаления
     * @return удалённая ссылка
     * @throws EntityNotFoundException если чат или ссылка не найдена
     * @throws EntityValidationFailedException если форма заполнена не верно
     */
    @NotNull Link untrackLink(long chatId, @NotNull RemoveLinkRequest removeLinkRequest)
        throws EntityNotFoundException, EntityValidationFailedException;

    /**
     * Получает обновление ресурсов ссылок, сохраняет и возвращает в виде обновлений для бота.
     * @param from С какого периода проверять обновления
     * @return Обновления для бота
     */
    @NotNull List<LinkUpdate> updateLinksFrom(@NotNull OffsetDateTime from);
}
