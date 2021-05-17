package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.sprint.SprintDto;
import cz.cvut.fel.tmr.dto.sprint.SprintReadDto;
import cz.cvut.fel.tmr.service.SprintService;
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

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/rest/sprints", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Tag(name = "Sprint", description = "Sprint API")
public class SprintRestController {

    @Autowired
    private SprintService service;

    @Operation(summary = "Add a new Sprint", tags = {"Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sprint created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Sprint already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveSprint(
            @Parameter(description = "Project", required = true) @NotNull @RequestBody SprintDto sprint
    ) {
        log.info("saveSprint() - start: sprint = {}", sprint);
        Long newProjectId = service.persist(sprint);
        log.info("saveSprint() - end: saved sprint = {}", sprint);
        return newProjectId;
    }

    @Operation(summary = "Add task to Sprint", tags = {"Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task added"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @PostMapping("/{sprintId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addTask(
            @Parameter(description = "Sprint Id", required = true) @PathVariable Long sprintId,
            @PathVariable Long taskId
    ) {
        log.info("addTask() - start");
        service.addTask(sprintId, taskId);
        log.info("addTask() - end");
    }

    @Operation(summary = "Remove task from Sprint", tags = {"Sprint", "Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task removed"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Access denied")})
    @DeleteMapping("/{sprintId}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void removeTask(
            @Parameter(description = "Sprint Id", required = true) @PathVariable Long sprintId,
            @PathVariable Long taskId
    ) {
        log.info("removeTask() - start");
        service.removeTask(sprintId, taskId);
        log.info("removeTask() - end");
    }

    @Operation(summary = "Find Sprint", description = " ", tags = {"Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SprintReadDto.class)))),
            @ApiResponse(responseCode = "404", description = "Sprint not found"),
            @ApiResponse(responseCode = "401", description = "Access denied")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SprintReadDto findSprint(
            @Parameter(description = "Id of the Sprint to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getSprintById() - start");
        SprintReadDto sprintReadDto = service.find(id);
        log.info("getSprintById() - end");
        return sprintReadDto;
    }

    @Operation(summary = "Update an existing Sprint", description = "need to fill", tags = {"Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "401", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Sprint not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateSprint(
            @Parameter(description = "Id of the Sprint to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Sprint to update.", required = true)
            @RequestBody SprintDto sprint) {
        log.info("refreshSprint() - start: id = {}, sprint = {}", id, sprint);
        service.update(id, sprint);
        log.info("refreshSprint() - end: sprint = {}", sprint);
    }

    @Operation(summary = "Delete an existing Sprint", description = "need to fill", tags = {"Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "401", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Sprint not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeSprint(
            @Parameter(description = "Id of the Sprint to be delete. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("removeSprint() - start: Sprint ID: {}", id);
        service.delete(id);
        log.info("removeSprint() - end: removed Sprint ID = {}", id);
    }
}
