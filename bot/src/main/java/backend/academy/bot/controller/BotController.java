package backend.academy.bot.controller;

import backend.academy.base.schema.ApiErrorResponse;
import backend.academy.base.schema.bot.LinkUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Bot Controller")
public interface BotController {

    String UPDATES_URI = "/updates";

    @Operation(summary = "Отправить обновление")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Обновление обработано"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Некорректные параметры запроса",
                        content =
                                @Content(
                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                        schema = @Schema(implementation = ApiErrorResponse.class)))
            })
    @PostMapping(UPDATES_URI)
    void sendUpdate(@RequestBody @Valid LinkUpdate update);
}
