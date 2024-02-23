package edu.java.controller;

import edu.java.controller.model.LinkUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdatesController {
    /**
     * POST /tg-chat/{id} : Отправить обновление
     *
     * @param linkUpdate (Request body) (required)
     * @return
     * Обновление обработано (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     */
    @PostMapping
    public ResponseEntity<String> receiveUpdates(@RequestBody LinkUpdate linkUpdate) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("wololo");
    }
}
