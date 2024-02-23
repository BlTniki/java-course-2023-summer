package edu.java.controller;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.LinkResponse;
import edu.java.controller.model.ListLinksResponse;
import edu.java.controller.model.RemoveLinkRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LinksController implements LinksApi {
    @Override
    public ResponseEntity<ListLinksResponse> getAll(Long tgChatId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<LinkResponse> registerLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<String> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
