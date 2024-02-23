package edu.java.controller;

import edu.java.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat")
public class TgChatController {

    /**
     * POST /tg-chat/{id} : Зарегистрировать чат
     *
     * @param id  (required)
     * @return Чат зарегистрирован (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     */
    @PostMapping("/{id}")
    public ResponseEntity<String> registerChat(@PathVariable long id) throws Exception {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("wololo");
    }

    /**
     * DELETE /tg-chat/{id} : Удалить чат
     *
     * @param id  (required)
     * @return Чат успешно удалён (status code 200)
     *         or Некорректные параметры запроса (status code 400)
     *         or Чат не существует (status code 404)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("wololo2");
    }
}
