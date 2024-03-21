package edu.java.bot.controller;

import edu.java.bot.controller.model.LinkUpdate;
import edu.java.bot.service.UpdatesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class UpdatesController implements UpdatesApi {
    private final UpdatesService updatesService;

    public UpdatesController(UpdatesService updatesService) {
        this.updatesService = updatesService;
    }

    @Override
    public ResponseEntity<Void> updatesPost(LinkUpdate body) {
        updatesService.processLinkUpdate(body);
        return ResponseEntity.ok().build();
    }
}
