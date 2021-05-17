package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.*;
import cz.cvut.fel.tmr.dto.project.ProjectDto;
import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
import cz.cvut.fel.tmr.dto.project.ProjectUserDto;
import cz.cvut.fel.tmr.exception.*;
import cz.cvut.fel.tmr.model.*;
import cz.cvut.fel.tmr.model.relations.ProjectUser;
import cz.cvut.fel.tmr.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
public class ProjectService {
    private final ProjectDao dao;
    private final OrganizationDao organizationDao;

    private final UserDao userDao;
    private final ProjectUserDao projectUserDao;

    private final SprintDao sprintDao;
    private final UserOrganizationDao userOrganizationDao;
    private final UserService userService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public ProjectService(ProjectDao dao, UserDao userDao, ProjectUserDao projectUserDao, SprintDao sprintDao, OrganizationDao organizationDao, UserOrganizationDao userOrganizationDao, UserService userService) {
        this.dao = dao;
        this.userDao = userDao;
        this.projectUserDao = projectUserDao;
        this.sprintDao = sprintDao;
        this.organizationDao = organizationDao;
        this.userOrganizationDao = userOrganizationDao;
        this.userService = userService;
    }


    /**
     * Persist project via its DTO.
     * Checks name to not be empty and name of the project does not exists
     *
     * @param project ProjectDto
     */
    @Transactional
    public Long persist(ProjectDto project) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(project.getName());
        if(project.getOrganizationId() == null) throw new EarException("Organization ID must be specified");

        User user = userService.getSecurityUser();

        // name must not be empty
        if (project.getName().isEmpty()) throw new ValidationException("Name of the project can not be empty");

        // project must not exist
        Project foundProject = dao.findByName(project.getName());
        if (foundProject != null) throw new AlreadyExistsException("Project with this name already exists");

        Organization foundOrganization = organizationDao.find(project.getOrganizationId());
        if (foundOrganization == null) throw new AlreadyExistsException("Organization with this id doesnt exist");

        if (!foundOrganization.isUserPM(user.getId())) {
            throw new ValidationException("Not enough rights");
        }
        Project newProject = project.buildNewProject();
        newProject.setOrganization(foundOrganization);
        dao.persist(newProject);

        // Add user to project
        ProjectUser projectUser = new ProjectUser(user, newProject);
        projectUserDao.persist(projectUser);

        return newProject.getId();
    }

    @Transactional
    public ProjectReadDto find(Long projectId) {
        Objects.requireNonNull(projectId);
        Project project = dao.find(projectId);
        if (project == null)
            throw new NotFoundException("Project not found");

        User user = userService.getSecurityUser();
        if (project.isUserInProject(user.getId()) || project.getOrganization().isUserPM(user.getId()))
            return new ProjectReadDto(project);
        else throw new ValidationException("Not enough rights");
    }


    @Transactional
    public void update(Long projectId, ProjectDto projectDto) {
        Objects.requireNonNull(projectDto);
        Project project = dao.find(projectId);
        if (project == null) throw new NotFoundException("Project does not exist");

        User user = userService.getSecurityUser();

        if (project.getOrganization().isUserPM(user.getId())) {
            project = projectDto.updateProject(project);
            dao.update(project);
        }
        throw new ValidationException("Not enough rights");

    }

    @Transactional
    public void deleteProject(Long projectId) {
        User user = userService.getSecurityUser();
        Project project = dao.find(projectId);
        if (project.getOrganization().isUserPM(user.getId()))
            dao.remove(project);
        throw new ValidationException("Not enough rights");
    }

    @Transactional
    public ProjectReadDto findByName(String projectName) {
        User user = userService.getSecurityUser();
        Project project = dao.findByName(projectName);
        if (project.isUserInProject(user.getId()) || project.getOrganization().isUserPM(user.getId()))
            return new ProjectReadDto(project);
        throw new ValidationException("Not enough rights");
    }


    @Transactional
    public void addUser(ProjectUserDto dto) throws NotFoundException {
        Project project = dao.find(dto.getProject());
        if (project == null) throw new NotFoundException("Project with ID " + dto.getProject() + " not found");

        User user = userDao.find(dto.getUser());
        if (user == null) throw new NotFoundException("User with ID " + dto.getUser() + " not found");

        User currentUser = userService.getSecurityUser();

        if ((project.isUserInProject(currentUser.getId()) || project.getOrganization().isUserPM(currentUser.getId())))
            projectUserDao.addUser(project, user);
        throw new ValidationException("Not enough rights");
    }

    @Transactional
    public void removeUser(Long projectId, Long userId) {
        User currentUser = userService.getSecurityUser();
        ProjectUser projectUser = projectUserDao.find(projectId, userId);
        if (currentUser.equals(projectUser.getUser()) || projectUser.getProject().getOrganization().isUserPM(currentUser.getId()))
            ;
        projectUserDao.remove(projectUser);
        throw new ValidationException("Not enough rights");

    }

}
