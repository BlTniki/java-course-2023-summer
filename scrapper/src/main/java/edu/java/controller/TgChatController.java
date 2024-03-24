package edu.java.controller;

import edu.java.domain.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TgChatController implements TgChatApi {
    private final ChatService chatService;

    public TgChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public ResponseEntity<Void> tgChatIdDelete(Long id) {
        chatService.removeChat(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> tgChatIdPost(Long id) {
        chatService.addChat(id);
        return ResponseEntity.ok().build();
    }
}
