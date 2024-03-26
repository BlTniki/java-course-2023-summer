package edu.java.controller;

import edu.java.controller.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SuppressWarnings("checkstyle:LineLength")
@Validated
@RequestMapping("/tg-chat")
public interface TgChatApi {

    @Operation(summary = "Удалить чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

        @ApiResponse(responseCode = "404", description = "Чат не существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

        @ApiResponse(responseCode = "429", description = "Исчерпан лимит запросов", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "{id}",
        produces = { "application/json" },
        method = RequestMethod.DELETE)
    ResponseEntity<Void> tgChatIdDelete(@Parameter(in = ParameterIn.PATH, schema = @Schema()) @PathVariable("id") Long id);


    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),

        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),

        @ApiResponse(responseCode = "429", description = "Исчерпан лимит запросов", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestMapping(value = "/{id}",
        produces = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<Void> tgChatIdPost(@Parameter(in = ParameterIn.PATH, schema = @Schema()) @PathVariable("id") Long id);
}

