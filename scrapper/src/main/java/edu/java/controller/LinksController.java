package edu.java.controller;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.LinkResponse;
import edu.java.controller.model.ListLinksResponse;
import edu.java.controller.model.RemoveLinkRequest;
import edu.java.service.link.LinkService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Profile("prod")
public class LinksController implements LinksApi {
    private final LinkService linkService;

    public LinksController(LinkService linkService) {
        this.linkService = linkService;
    }

    @Override
    public ResponseEntity<ListLinksResponse> getAll(Long tgChatId) {
        return ResponseEntity.ok(ListLinksResponse.from(linkService.getByChatId(tgChatId)));
    }

    @Override
    public ResponseEntity<LinkResponse> registerLink(Long tgChatId, AddLinkRequest addLinkRequest) {
        return ResponseEntity.ok(LinkResponse.from(linkService.trackLink(tgChatId, addLinkRequest)));
    }

    @Override
    public ResponseEntity<LinkResponse> deleteLink(Long tgChatId, RemoveLinkRequest removeLinkRequest) {
        return ResponseEntity.ok(LinkResponse.from(linkService.untrackLink(tgChatId, removeLinkRequest)));
    }
}
