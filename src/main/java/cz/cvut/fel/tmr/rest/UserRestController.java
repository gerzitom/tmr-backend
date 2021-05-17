package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.task.TaskReadDto;
import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeReadDto;
import cz.cvut.fel.tmr.dto.user.UserDto;
import cz.cvut.fel.tmr.dto.user.UserLoginDto;
import cz.cvut.fel.tmr.dto.user.UserReadDto;
import cz.cvut.fel.tmr.service.TaskService;
import cz.cvut.fel.tmr.service.TrackedTimeService;
import cz.cvut.fel.tmr.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/rest/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "Users API")
@CrossOrigin
public class UserRestController {
    private final UserService userService;

    private final TrackedTimeService trackedTimeService;

    private final TaskService taskService;

    @Operation(summary = "Login user", description = "endpoint for creating an entity", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
    })
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> loginUser(
            @Parameter(description = "User", required = true) @NotNull @RequestBody UserLoginDto user) {
        log.info("loginUser() - start: User = {}", user);
        String token = userService.login(user);
        log.info("loginUser() - end: token id = {}", token);
        Map<String, String> ret = new HashMap<>();
        ret.put("token", token);
        return ret;
    }

    @Operation(summary = "Get authenticated user", description = " ", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class))))})
    @GetMapping("/authenticateduser")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin
    public UserReadDto getAuthenticatedUser(
            @Parameter(description = "Authentication header", required = true)
            @NotNull @RequestHeader("Authorization") String token
    ) {
        log.info("getUserById() - start");
        UserReadDto user = userService.getAuthenticatedUser(token);
        log.info("getUserById() - end");
        return user;
    }


    @Operation(summary = "Add a new User", description = "endpoint for creating an entity", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveUser(
            @Parameter(description = "User", required = true) @NotNull @RequestBody UserDto user) {
        log.info("saveUser() - start: User = {}", user);
        Long userId = userService.persist(user);
        log.info("saveUser() - end: savedUser id = {}", userId);
        return userId;
    }

    @Operation(summary = "Save user avatar", description = "endpoint for creating an entity", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User avatar created")})
    @PostMapping("/{id}/avatar")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUserAvatar(
            @Parameter(description = "Id of the User to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Avatar image", required = true) @NotNull @RequestParam("image") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        log.info("saveUserAvatar() - start");
        userService.setUserAvatar(id, file);
        log.info("saveUserAvatar() - end");
    }

    @Operation(summary = "Find all Users", description = " ", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class))))})
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Map<Long, UserReadDto> getAllUsers() {
        log.info("getAllUsers() - start");
        Map<Long, UserReadDto> collection = userService.findAll();
        log.info("getAllUsers() - end");
        return collection;
    }

    @Operation(summary = "Find User", description = " ", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class))))})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserReadDto getUserById(
            @Parameter(description = "Id of the User to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getUserById() - start");
        UserReadDto user = userService.find(id);
        log.info("getUserById() - end");
        return user;
    }

    @Operation(summary = "Update an existing User", description = "Process only, if logged in user is admin or user itself", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Automobile not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void refreshUser(
            @Parameter(description = "Id of the User to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "User to update.", required = true)
            @RequestBody UserDto user) {
        log.info("refreshUser() - start: id = {}, automobile = {}", id, user);
        userService.update(id, user);
        log.info("refreshUser() - end: updatedAutomobile = {}", user);
    }

    @Operation(summary = "Delete an existing User", description = "need to fill", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Automobile not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(
            @Parameter(description = "Id of the User to be delete. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("removeUser() - start: user ID: {}", id);
        userService.remove(id);
        log.info("removeUser() - end: removed user ID = {}", id);
    }


    /**
     * Gets tracked time object if some is active, this means tracked time has null end value
     * If nothing is active, returns null
     *
     * @param id
     * @return
     */
    @Operation(summary = "Find User", description = "Gets tracked time object if some is active, this means tracked time has null end value. If nothing is active, returns null. ", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackedTimeReadDto.class))))})
    @GetMapping("/{id}/activetracking")
    @ResponseStatus(HttpStatus.OK)
    public TrackedTimeReadDto getUserActiveTracking(
            @Parameter(description = "Id of the User to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getUserActiveTracking() - start");
        TrackedTimeReadDto trackedTimeReadDto = trackedTimeService.findByUserActiveUse(id);
        log.info("getUserActiveTracking() - end");
        return trackedTimeReadDto;
    }

    @Operation(summary = "Find Upcomming tasks", description = "", tags = {"User"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrackedTimeReadDto.class))))})
    @GetMapping("/{id}/upcomming_tasks")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskReadDto> getUserUpcommingTasks(
            @Parameter(description = "Id of the User to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("getUserActiveTracking() - start");
        List<TaskReadDto> tasks = userService.getUpcommingTasks(id);
        log.info("getUserActiveTracking() - end");
        return tasks;
    }
}
