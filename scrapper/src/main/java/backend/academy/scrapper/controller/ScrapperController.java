package backend.academy.scrapper.controller;

import backend.academy.base.schema.ApiErrorResponse;
import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.base.schema.scrapper.TagsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Scrapper Controller")
public interface ScrapperController {

    String TG_CHAT_URI = "/tg-chat/{id}";
    String ID_VAR = "id";

    String LINKS_URI = "/links";
    String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    String TAGS_URI = "/tags";

    @Operation(summary = "Зарегистрировать чат")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Чат зарегистрирован"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping(TG_CHAT_URI)
    void registerChat(@PathVariable(ID_VAR) @Positive Long id);

    @Operation(summary = "Удалить чат")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Чат успешно удалён"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Чат не существует",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @DeleteMapping(TG_CHAT_URI)
    void deleteChat(@PathVariable(ID_VAR) @Positive Long id);

    @Operation(summary = "Получить теги")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Теги успешно получены",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = TagsResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @GetMapping(TAGS_URI)
    TagsResponse getTags(@RequestHeader(TG_CHAT_ID_HEADER) @Positive Long chatId);

    @Operation(summary = "Добавить тег")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Тег успешно добавлен",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping(TAGS_URI)
    void addTag(@RequestHeader(TG_CHAT_ID_HEADER) @Positive Long chatId, @RequestBody @Valid AddTagRequest request);

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылки успешно получены",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ListLinksResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @GetMapping(LINKS_URI)
    ListLinksResponse getLinks(@RequestHeader(TG_CHAT_ID_HEADER) @Positive Long chatId);

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылка успешно добавлена",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = LinkResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping(LINKS_URI)
    LinkResponse addLink(
            @RequestHeader(TG_CHAT_ID_HEADER) @Positive Long chatId, @RequestBody @Valid AddLinkRequest request);

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Ссылка успешно убрана",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = LinkResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Ссылка не найдена",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @DeleteMapping(LINKS_URI)
    LinkResponse removeLink(
            @RequestHeader(TG_CHAT_ID_HEADER) @Positive Long chatId, @RequestBody @Valid RemoveLinkRequest request);
}
