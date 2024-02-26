package edu.java.controller;

import edu.java.controller.model.AddLinkRequest;
import edu.java.controller.model.ErrorResponse;
import edu.java.controller.model.LinkResponse;
import edu.java.controller.model.ListLinksResponse;
import edu.java.controller.model.RemoveLinkRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SuppressWarnings("checkstyle:LineLength")
@Validated
@RequestMapping("/links")
public interface LinksApi {
    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылки успешно получены", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ListLinksResponse.class))),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @RequestMapping(produces = { "application/json" },
                    method = RequestMethod.GET)
   ResponseEntity<ListLinksResponse> getAll(
            @Parameter(in = ParameterIn.HEADER) @RequestHeader(value = "Tg-Chat-Id") Long tgChatId
    );

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @RequestMapping(produces = { "application/json" },
                    consumes = { "application/json" },
                    method = RequestMethod.POST)
    ResponseEntity<LinkResponse> registerLink(
        @Parameter(in = ParameterIn.HEADER) @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Parameter(in = ParameterIn.DEFAULT) @Valid @RequestBody AddLinkRequest addLinkRequest
    );

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LinkResponse.class))),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

        @ApiResponse(responseCode = "404", description = "Ссылка не найдена", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))) })
    @RequestMapping(produces = { "application/json" },
                    consumes = { "application/json" },
                    method = RequestMethod.DELETE)
    ResponseEntity<LinkResponse> deleteLink(
        @Parameter(in = ParameterIn.HEADER) @RequestHeader(value = "Tg-Chat-Id") Long tgChatId,
        @Parameter(in = ParameterIn.DEFAULT) @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    );
}
