package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.comment.CommentDto;
import cz.cvut.fel.tmr.dto.comment.CommentReadDto;
import cz.cvut.fel.tmr.dto.task.TaskDto;
import cz.cvut.fel.tmr.dto.task.TaskReadDto;
import cz.cvut.fel.tmr.dto.task.TaskUserDto;
import cz.cvut.fel.tmr.model.Comment;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.service.CommentService;
import cz.cvut.fel.tmr.service.ProjectService;
import cz.cvut.fel.tmr.service.TaskService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task", description = "Tasks API")
@Api(value = "Task controller", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class TaskRestController {

    private final TaskService taskService;

    private final ProjectService projectService;

    private final CommentService commentService;

    private final TrackedTimeService trackedTimeService;

    @Operation(summary = "Add a new Task", description = "endpoint for creating an entity", tags = {"Task"})
    @ApiOperation("Saves task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveTask(
            @Parameter(description = "Task", required = true) @NotNull @RequestBody TaskDto task) {
        log.info("saveTask() - start: task = {}", task);
        Long newTaskId = taskService.persist(task);
        log.info("saveTask() - end: savedTask = {}", task);
        return newTaskId;
    }

    @Operation(summary = "Find all Tasks", description = "Gets all tasks for logged in user", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class))))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskReadDto> getUserTasks() {
        log.info("getUserTasks() - start");
        List<TaskReadDto> userTasks = taskService.findAll();
        log.info("getUserTasks() - end");
        return userTasks;
    }

    @Operation(summary = "Find task", description = " ", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class))))})
    @GetMapping("/{id}")
    @CrossOrigin
    @ResponseStatus(HttpStatus.OK)
    public TaskReadDto getTaskById(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getTaskById() - start");
        TaskReadDto task = taskService.find(id);
        log.info("getTaskById() - end");
        return task;
    }

    @Operation(summary = "Update an existing Tasks", description = "need to fill", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void refreshTask(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Task to update.", required = true)
            @RequestBody TaskDto task) {
        log.info("refreshAutomobile() - start: id = {}, automobile = {}", id, task);
        taskService.update(id, task);
        log.info("refreshAutomobile() - end: updatedAutomobile = {}", task);
    }

    @Operation(summary = "Delete an existing Tasks", description = "need to fill", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
    })
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeTask(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long taskId
    ) {
        log.info("removeTask() - start: id = {}", taskId);
        taskService.removeTask(taskId);
        log.info("removeTask() - end: removed task = {}", taskId);
    }

    @Operation(summary = "Add subtask to task", description = "endpoint for creating an entity", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @PostMapping("/{id}/subtasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSubtask(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Comment", required = true) @NotNull @RequestBody TaskDto task) {
        log.info("addSubtask() - start: task = {}", task);
        taskService.addSubtask(id, task);
        log.info("addSubtask() - end: updated Task = {}", task );
    }

    @Operation(summary = "Add comment to task", description = "endpoint for creating an entity", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void addComment(
        @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
        @PathVariable Long id,
        @Parameter(description = "Comment", required = true)
        @NotNull @RequestBody CommentDto comment
    ) {
        log.info("addComment() - start: task = {}", comment);
        taskService.addComment(id, comment);
        log.info("addComment() - end: updated Comment = {}", comment);
    }

    @Operation(summary = "Find commnets in task", description = " ", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Comment.class))))})
    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentReadDto> getTaskComments(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getTaskComments() - start");
        List<CommentReadDto> comments = commentService.findAll(id).stream().map(CommentReadDto::new).collect(Collectors.toList());
        log.info("getTaskComments() - end");
        return comments;
    }

    @Operation(summary = "Remove user from task", description = "endpoint for removing user from task", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeComment(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long commentId
    ) {
        log.info("removeComment() - start: comment ID: {}", commentId);
        taskService.removeComment(commentId);
        log.info("removeComment() - end");
    }

    @Operation(summary = "Update an existing Tasks", description = "need to fill", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateComment(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long commentId,
            @Parameter(description = "Task to update.", required = true)
            @RequestBody CommentDto dto
    ) {
        log.info("updateComment() - start: id = {}, dto = {}", commentId, dto);
        taskService.updateComment(commentId, dto);
        log.info("updateComment() - end");
    }

    @Operation(summary = "Add user to task", description = "endpoint for adding user to task", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void addAssignee(
            @Parameter(description = "Comment", required = true) @NotNull @RequestBody TaskUserDto dto) {
        log.info("addComment() - start: task = {}", dto);
        taskService.addAssignee(dto);
        log.info("addComment() - end: updated Comment = {}", dto);
    }

    @Operation(summary = "Remove user from task", description = "endpoint for removing user from task", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Task already exists")})
    @DeleteMapping("/{taskId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeAssignee(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long taskId,
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long userId
    ) {
        log.info("removeUser() - start: task ID: {}, user ID: {}", taskId, userId);
        taskService.removeAssignee(taskId, userId);
        log.info("removeUser() - end");
    }

    /**
     * @param id Id of task
     * @return Map where key is user id and value is elapsed time on project in seconds
     */
    @Operation(summary = "Get tracked time overview for task", description = " ", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Map.class))))})
    @GetMapping("/{id}/trackedtime")
    @ResponseStatus(HttpStatus.OK)
    public Map<Long, Long> getTaskTrackedTime(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getTaskTrackedTime() - start");
        Map<Long, Long> ret = trackedTimeService.getTaskTrackedTimeBrief(id);
        log.info("getTaskTrackedTime() - end");
        return ret;
    }
}
