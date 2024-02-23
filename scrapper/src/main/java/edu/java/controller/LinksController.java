package edu.java.controller;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.LinkResponse;
import edu.java.controller.model.ListLinksResponse;
import edu.java.controller.model.RemoveLinkRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("/links")
public class LinksController {

    /**
     * GET /links : Получить все ссылки
     *
     * @param tgChatId (Header 'Tg-Chat-Id') (required)
     * @return Ссылки успешно получены (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     */
    @GetMapping
    public ResponseEntity<ListLinksResponse> getAll(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId
        ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
            new ListLinksResponse(
                List.of(),
                0
            )
        );
    }

    /**
     * Post /links : Добавить ссылку
     *
     * @param tgChatId (Header 'Tg-Chat-Id') (required)
     * @param addLinkRequest (Request body) (required)
     * @return Ссылки успешно получены (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     */
    @PostMapping
    public ResponseEntity<LinkResponse> registerLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
            @RequestBody AddLinkRequest addLinkRequest
        ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }

    /**
     * Delete /links : Удалить ссылку
     *
     * @param tgChatId (Header 'Tg-Chat-Id') (required)
     * @param removeLinkRequest (Request body) (required)
     * @return Ссылка успешно убрана (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     *         or Ссылка не найдена (status code 404)
     */
    @DeleteMapping
    public ResponseEntity<String> deleteLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
            @RequestBody RemoveLinkRequest removeLinkRequest
        ) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(null);
    }
}
