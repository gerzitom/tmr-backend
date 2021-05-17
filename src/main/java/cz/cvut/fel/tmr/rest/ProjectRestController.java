package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.column.ColumnReadDto;
import cz.cvut.fel.tmr.dto.project.ProjectDto;
import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
import cz.cvut.fel.tmr.dto.project.ProjectUserDto;
import cz.cvut.fel.tmr.dto.sprint.SprintDto;
import cz.cvut.fel.tmr.dto.sprint.SprintReadDto;
import cz.cvut.fel.tmr.dto.task.TaskReadDto;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.Sprint;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.TaskColumn;
import cz.cvut.fel.tmr.service.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest/projects", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Tag(name = "Project", description = "Project API")
public class ProjectRestController {

    private final ProjectService service;

    private final TaskService taskService;

    private final SprintService sprintService;

    private final ColumnService columnService;


//Updated: GetProjects() je v OrganizationController, user muze podivat na projekty jen v organizaci. Musi byt akceptavano.

//    @Operation(summary = "Find all Projects", description = "Gets all project, where currently logged user is enlisted. If user has admin or project manager rules, all projects are displayed.", tags = {"Project"})
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "successful operation",
//                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Project.class)))),
//            @ApiResponse(responseCode = "403", description = "JWT expired or other authentication error")
//    })
//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    public List<ProjectReadDto> getAllProjects(){
//        log.info("getAllProjects() - start");
//        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        List<ProjectReadDto> allProjects = service.getAll(user);
//        log.info("getAllProjects() - end");
//        return allProjects;
//    }

    @Operation(summary = "Add a new Project", description = "Creates project and adds user, who is logged in", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Project already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveProject(
            @Parameter(description = "Project", required = true) @NotNull @RequestBody ProjectDto project) {
        log.info("saveProject() - start: project = {}", project);
        Long newProjectId = service.persist(project);
        log.info("saveProject() - end: saved project = {}", project);
        return newProjectId;
    }

    @Operation(summary = "Get project by ID", description = " ", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Project.class))))})
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectReadDto getProject(
            @Parameter(description = "Id of the Project to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getProject() - start");
        ProjectReadDto project = service.find(id);
        log.info("getProject() - end");
        return project;
    }

    @Operation(summary = "Update an existing Project", description = "need to fill", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProject(
            @Parameter(description = "Id of the Project to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Project to update.", required = true)
            @RequestBody ProjectDto projectDto) {
        log.info("updateProject() - start: id = {}", id);
        service.update(id, projectDto);
        log.info("updateProject() - end:");
    }

    @Operation(summary = "Update an existing Project", description = "need to fill", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProject(
            @Parameter(description = "Id of the Task to be update. Cannot be empty.", required = true)
            @PathVariable Long projectId
    ) {
        log.info("deleteProject() - start: id = {}", projectId);
        service.deleteProject(projectId);
        log.info("deleteProject() - end:");
    }

    @Operation(summary = "Find project task", description = " ", tags = {"Task"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Task.class))))})
    @GetMapping("/{id}/tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskReadDto> getTasksOfProject(
            @Parameter(description = "Id of the Project to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getTasksOfProject() - start");
        List<TaskReadDto> result = taskService.findByProject(id).stream()
                .map(TaskReadDto::new)
                .filter(task -> task.getParentTaskId() == null)
                .collect(Collectors.toList());
        log.info("getTasksOfProject() - end");
        return result;
    }

    @Operation(summary = "Find project columns", description = " ", tags = {"TaskColumn"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskColumn.class))))})
    @GetMapping("/{id}/columns")
    @ResponseStatus(HttpStatus.OK)
    public List<ColumnReadDto> getColumnsOfProject(
            @Parameter(description = "Id of the Project to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getColumnsOfProject() - start");
        List<ColumnReadDto> result = columnService.findByProject(id).stream()
                .map(ColumnReadDto::new)
                .collect(Collectors.toList());
        log.info("getColumnsOfProject() - end");
        return result;
    }

    @Operation(summary = "Add a User to project", description = "endpoint for creating an entity", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(
            @Parameter(description = "Project", required = true) @NotNull @RequestBody ProjectUserDto dto) {
        log.info("addUser() - start: project = {}", dto);
        service.addUser(dto);
        log.info("addUser() - end: saved project = {}", dto);
    }

    @Operation(summary = "Add a User to project", description = "endpoint for creating an entity", tags = {"Project"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @DeleteMapping("/{projectId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long projectId,
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long userId
    ) {
        log.info("removeUser() - start");
        service.removeUser(projectId, userId);
        log.info("removeUser() - end");
    }

    @Operation(summary = "Find active project sprint", tags = {"Project", "Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sprint.class))))})
    @GetMapping("/{projectId}/active_sprint")
    @ResponseStatus(HttpStatus.OK)
    public SprintReadDto getActiveSprint(@Parameter(description = "Id of the Project to be obtained. Cannot be empty.", required = true)
                                    @PathVariable Long projectId){
        log.info("getActiveSprint() - start");
        log.info("getProject() - end");
        return sprintService.findCurrentSprint(projectId);
    }

    @Operation(summary = "Find all project sprints", tags = {"Project", "Sprint"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Sprint.class))))})
    @GetMapping("/{projectId}/sprints")
    @ResponseStatus(HttpStatus.OK)
    public List<SprintReadDto> getSprints(@Parameter(description = "Id of the Project to be obtained. Cannot be empty.", required = true)
                                         @PathVariable Long projectId){
        log.info("getSprints() - start");
        log.info("getSprints() - end");
        return sprintService.findSprints(projectId);
    }
}
