package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeDto;
import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeReadDto;
import cz.cvut.fel.tmr.service.TrackedTimeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/trackedtime")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tracked time", description = "Tasks API")
@Api(value = "Tracked time controller", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class TrackedRestController {
    private final TrackedTimeService service;

    @Operation(summary = "Add a new Tracked time", description = "endpoint for creating an entity", tags = {"Tracked time"})
    @ApiOperation("Saves task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tracked time created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User has already tracking something")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveTrackedTime(
            @Parameter(description = "Tracked time", required = true) @NotNull @RequestBody TrackedTimeDto dto) {
        log.info("saveTrackedTime() - start: task = {}", dto);
        Long trackedTimeId = service.addTrackedTime(dto);
        log.info("saveTrackedTime() - end: savedTask = {}", trackedTimeId);
        return trackedTimeId;
    }

    @Operation(summary = "Find all Tracked times", description = " ", tags = {"Tracked time"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackedTimeReadDto.class))))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TrackedTimeReadDto>> getAllTrackedTimes() {
        log.info("getAllTrackedTimes() - start");
        List<TrackedTimeReadDto> collection = service.findAll();
        log.info("getAllTrackedTimes() - end");
        return new ResponseEntity<>(collection, HttpStatus.OK);
    }


    @Operation(summary = "Update an existing Tracked time", description = "need to fill", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Tracked time not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateTrackedTime(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Task to update.", required = true)
            @RequestBody TrackedTimeDto trackedTimeDto) {
        log.info("updateTrackedTime() - start: id = {}, dto = {}", id, trackedTimeDto);
        service.update(id, trackedTimeDto);
        log.info("updateTrackedTime() - end");
    }

    @Operation(summary = "Delete an existing Tracked time", description = "Deleting endpoint for tracked time", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Tracked time not found"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrackedTime(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("deleteTrackedTime() - start: id = {}", id);
        service.remove(id);
        log.info("deleteTrackedTime() - end: removed task = {}", id);
    }

}
