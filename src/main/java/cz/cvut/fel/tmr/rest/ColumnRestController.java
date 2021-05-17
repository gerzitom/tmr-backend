package cz.cvut.fel.tmr.rest;


import cz.cvut.fel.tmr.dto.column.ColumnDto;
import cz.cvut.fel.tmr.dto.column.ColumnReadDto;
import cz.cvut.fel.tmr.dto.sprint.SprintReadDto;
import cz.cvut.fel.tmr.dto.task.TaskReadDto;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.service.ColumnService;
import cz.cvut.fel.tmr.service.TaskService;
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

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest/columns", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Tag(name = "TaskColumn", description = "TaskColumn API")
public class ColumnRestController {

    @Autowired
    private ColumnService columnService;
    @Autowired
    private TaskService taskService;


    @Operation(summary = "Add a new TaskColumn", description = "Creates column within a defined Project, who is logged in", tags = {"TaskColumn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "TaskColumn created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "TaskColumn already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
   // @RolesAllowed({ "PROJECT_MANAGER", "ADMIN" })
    public Long createColumn(
            @Parameter(description = "TaskColumn", required = true) @NotNull @RequestBody ColumnDto columnDto
            ) {
        log.info("saveColumn() - start: column = {}", columnDto);
        Long newColumnId = columnService.persist(columnDto);
        log.info("saveColumn() - end: saved column = {}", columnDto);
        return newColumnId;
    }

    @Operation(summary = "Find TaskColumn", description = " ", tags = {"TaskColumn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SprintReadDto.class))))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ColumnReadDto findColumn(
            @Parameter(description = "Id of the TaskColumn to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getColumnById() - start");
        ColumnReadDto columnReadDto = columnService.find(id);
        log.info("getSprintById() - end");
        return columnReadDto;
    }

    @Operation(summary = "Update an existing TaskColumn", description = "need to fill", tags = {"TaskColumn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "TaskColumn not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/{id}")
    //@RolesAllowed({ "PROJECT_MANAGER", "ADMIN" })
    @ResponseStatus(HttpStatus.OK)
    public void updateColumn(
            @Parameter(description = "Id of the TaskColumn to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "TaskColumn to update.", required = true)
            @RequestBody ColumnDto columnDto) {
        log.info("refreshSprint() - start: id = {}, column = {}", id, columnDto);
        columnService.update(id, columnDto);
        log.info("refreshSprint() - end: sprint = {}", columnDto);
    }

    @Operation(summary = "Delete an existing TaskColumn", description = "need to fill", tags = {"TaskColumn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "TaskColumn not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @DeleteMapping("/{id}")
    @RolesAllowed({ "PROJECT_MANAGER", "ADMIN" })
    @ResponseStatus(HttpStatus.OK)
    public void removeColumn(
            @Parameter(description = "Id of the TaskColumn to be delete. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("removeColumn() - start: TaskColumn ID: {}", id);
        columnService.removeColumn(id);
        log.info("removeSprint() - end: removed TaskColumn ID = {}", id);
    }

    @Operation(summary = "Find column task", description = " ", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class))))})
    @GetMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskReadDto> getTasksOfColumn(
            @Parameter(description = "Id of the column to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getTasksOfColumn() - start");
        List<TaskReadDto> result = taskService.findByColumn(id).stream()
                .map(TaskReadDto::new)
                .filter(task -> task.getParentTaskId() == null)
                .collect(Collectors.toList());
        log.info("getTasksOfColumn() - end");
        return result;
    }
}
