package cz.cvut.fel.tmr.rest;

import cz.cvut.fel.tmr.dto.organization.OrganizationDto;
import cz.cvut.fel.tmr.dto.organization.OrganizationReadDto;
import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
import cz.cvut.fel.tmr.dto.user.UserReadDto;
import cz.cvut.fel.tmr.model.Organization;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.SecurityUser;
import cz.cvut.fel.tmr.service.OrganizationService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@Tag(name = "Organization", description = "Organization API")
public class OrganizationRestController {

    private final OrganizationService service;

    @Operation(summary = "Add a new Organization", description = "Creates organization and adds founder, who is logged in", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organization created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Organization already exists")})
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveOrganization(
            @Parameter(description = "Organization", required = true) @NotNull @RequestBody OrganizationDto organization
    ) {
        log.info("saveOrganization() - start: organization = {}", organization);
        Long newOrganizationId = service.persist(organization);
        log.info("saveOrganization() - end: saved organization = {}", organization);
        return newOrganizationId;
    }

    @Operation(summary = "Find all Organizations by User", description = "Gets all Organizations, where currently user is member.", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Organization.class)))),
            @ApiResponse(responseCode = "403", description = "JWT expired or other authentication error")
    })
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<OrganizationReadDto> getAllOrganizationsByUser() {
        log.info("getAllOrganizationsByUser() - start");
        List<OrganizationReadDto> allOrganizations = service.getAllByUser();
        log.info("getAllOrganizationsByUser() - end");
        return allOrganizations;
    }

    @Operation(summary = "Get organization by ID", description = " ", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Organization.class))))})
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrganizationReadDto getOrganization(
            @Parameter(description = "Id of the Organization to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id) {
        log.info("getOrganization() - start");
        OrganizationReadDto organization = service.find(id);
        log.info("getOrganization() - end");
        return organization;
    }

    @Operation(summary = "Update an existing Organization", description = "Update name of organization, required unique", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Organization not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateOrganization(
            @Parameter(description = "Id of the Organization to be update. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Organization to update.", required = true)
            @RequestBody OrganizationDto organizationDto) {
        log.info("updateOrganization() - start: id = {}", id);
        service.update(id, organizationDto);
        log.info("updateOrganization() - end:");
    }

    @Operation(summary = "Delete an existing organization", description = "Delete organizations with all  projects", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Organization not found"),
            @ApiResponse(responseCode = "405", description = "Validation exception")})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrganization(
            @Parameter(description = "Id of the Organization to be delete. Cannot be empty.", required = true)
            @PathVariable Long id
    ) {
        log.info("deleteOrganization() - start: id = {}", id);
        service.delete(id);
        log.info("deleteOrganization() - end:");
    }

    @Operation(summary = "Add a User to organization", description = "Add user to organization with user role", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User added"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @PostMapping("/{id}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUser(@Parameter(description = "Id of the Organization to be delete. Cannot be empty.", required = true)
                        @PathVariable Long id,
                        @Parameter(description = "User", required = true) @PathVariable Long userId){
        log.info("addUser() - start: organization id = {}", id);
        service.addUser(id, userId);
        log.info("addUser() - end: saved organization id = {}", id);
    }

    @Operation(summary = "Delete a User from organization", description = "Delete a User from organization", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @DeleteMapping("/{id}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Id of the Task to be obtained. Cannot be empty.", required = true)
            @PathVariable Long userId
    ) {
        log.info("deleteUser() - start");
        service.removeUser(id, userId);
        log.info("deleteUser() - end: ");
    }

    @Operation(summary = "Change user role in organization", description = "Change role", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role changed"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already exists")})
    @PutMapping("/{id}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void changeRole(
            @Parameter(description = "Id of the Organization. Cannot be empty.", required = true)
            @PathVariable Long id,
            @Parameter(description = "Id of the User to change role. Cannot be empty.", required = true)
            @PathVariable Long userId,
            @Parameter(description = "User Role", required = true) @NotNull @RequestBody String role) {
        log.info("changeRole() - start: organization = {}", id);
        service.changeRole(id, userId, role);
        log.info("changeRole() - end: saved organization = {}", id);
    }

    @Operation(summary = "Get projects", description = "Get all projects in this organization", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Project.class))))})
    @GetMapping("{id}/projects")
    @ResponseStatus(HttpStatus.OK)
    public List<ProjectReadDto> getProjects(
            @Parameter(description = "Id of the Organization to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id) {
        log.info("getProjects() - start");
        List<ProjectReadDto> projects = service.getAllProjects(id);
        log.info("getProjects() - end");
        return projects;
    }

    @Operation(summary = "Get users", description = "Get all user with roles in this organization", tags = {"Organization"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserReadDto.class))))})
    @GetMapping("{id}/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserReadDto> getUsers(
            @Parameter(description = "Id of the Organization to be obtained. Cannot be empty.", required = true)
            @PathVariable Long id) {
        log.info("getUsers() - start");
        List<UserReadDto> users = service.getUsers(id);
        log.info("getUsers() - end");
        return users;
    }
}
