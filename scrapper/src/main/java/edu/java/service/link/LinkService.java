package edu.java.service.link;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.service.exception.EntityAlreadyExistException;
import edu.java.service.exception.EntityNotFoundException;
import edu.java.service.exception.EntityValidationFailedException;
import edu.java.service.link.model.Link;
import java.util.List;

public interface LinkService {
    /**
     * Возвращает все отслеживаемые данным чатом ссылки.
     * @param chatId id чата
     * @return Список отслеживаемых ссылок
     * @throws EntityNotFoundException если данный чат не найден
     */
    List<Link> getByChatId(long chatId) throws EntityNotFoundException;

    /**
     * Добавляет ссылку на отслеживание для данного чата.
     * @param chatId id чата
     * @param addLinkRequest форма для добавления
     * @return добавленная ссылка
     * @throws EntityNotFoundException если данный чат не найден
     * @throws EntityAlreadyExistException если данная ссылка уже добавлена
     * @throws EntityValidationFailedException если форма заполнена не верно или данная ссылка не поддерживается
     */
    Link trackLink(Long chatId, AddLinkRequest addLinkRequest)
        throws EntityNotFoundException, EntityAlreadyExistException, EntityValidationFailedException;

    /**
     * Убирает ссылку из отслеживаемых для данного чата.
     * @param chatId id чата
     * @param removeLinkRequest форма для удаления
     * @return удалённая ссылка
     * @throws EntityNotFoundException если чат или ссылка не найдена
     * @throws EntityValidationFailedException если форма заполнена не верно
     */
    Link untrackLink(Long chatId, RemoveLinkRequest removeLinkRequest)
        throws EntityNotFoundException, EntityValidationFailedException;
}
