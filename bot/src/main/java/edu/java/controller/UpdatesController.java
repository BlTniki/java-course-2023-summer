package edu.java.controller;

import edu.java.controller.model.LinkUpdate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdatesController implements UpdatesApi {
    @Override
    public ResponseEntity<Void> updatesPost(LinkUpdate body) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
