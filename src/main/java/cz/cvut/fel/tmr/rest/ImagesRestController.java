package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest/images", produces = MediaType.IMAGE_JPEG_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Tag(name = "Image", description = "Image API")
public class ImagesRestController {

    @Autowired
    private ImageService service;

    @Operation(summary = "Get user avatar", description = " ", tags = {"Image"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Project.class))))})
    @GetMapping("/avatar/{imageName}")
    @ResponseStatus(HttpStatus.OK)
    public byte[] getUserAvatar(
            @Parameter(description = "Name of the avatar image", required = true)
            @PathVariable String imageName
    ){
        log.info("getUserAvatar() - start");
        byte[] ret = service.getUserAvatar(imageName);
        log.info("getUserAvatar() - end");
        return ret;
    }
}
