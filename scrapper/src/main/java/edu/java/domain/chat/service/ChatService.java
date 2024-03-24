package edu.java.domain.chat.service;

import edu.java.domain.exception.EntityAlreadyExistException;
import edu.java.domain.exception.EntityNotFoundException;

public interface ChatService {
    /**
     * Регистрирует чат в приложении.
     * @param id telegram id чата
     * @throws EntityAlreadyExistException если чат с таким id уже зарегистрирован
     */
    void addChat(long id) throws EntityAlreadyExistException;

    /**
     * Удаляет чат в приложении.
     * @param id telegram id чата
     * @throws EntityNotFoundException если чата с таким id не существует
     */
    void removeChat(long id) throws EntityNotFoundException;
}
