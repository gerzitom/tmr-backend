package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.*;
import cz.cvut.fel.tmr.dto.organization.OrganizationDto;
import cz.cvut.fel.tmr.dto.organization.OrganizationReadDto;
import cz.cvut.fel.tmr.dto.project.ProjectDto;
import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
import cz.cvut.fel.tmr.dto.user.UserReadDto;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.AuthenticationException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.exception.ValidationException;
import cz.cvut.fel.tmr.model.*;
import cz.cvut.fel.tmr.model.relations.ProjectUser;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    private OrganizationDao organizationDao;
    private UserDao userDao;
    private UserService userService;
    private ProjectUserDao projectUserDao;
    private UserOrganizationDao userOrganizationDao;


    @Autowired
    public OrganizationService(OrganizationDao organizationDao, UserDao userDao, UserService userService, UserOrganizationDao userOrganizationDao,ProjectUserDao projectUserDao) {
        this.organizationDao = organizationDao;
        this.userDao = userDao;
        this.userService = userService;
        this.projectUserDao = projectUserDao;
        this.userOrganizationDao = userOrganizationDao;

    }

    @Transactional
    public Long persist(OrganizationDto organization) {
        Objects.requireNonNull(organization);

        User user = userService.getSecurityUser();

        // name must not be empty
        if (organization.getName().isEmpty())
            throw new ValidationException("Name of the organization can not be empty");

        // organization must not exist
        Organization foundOrganization = organizationDao.findByName(organization.getName());
        if (foundOrganization != null) throw new AlreadyExistsException("Organization with this name already exists");

        Organization newOrganization = organization.buildNewOrganization();
        newOrganization.setFounder(user);

        userOrganizationDao.addUser(newOrganization, user, Role.PROJECT_MANAGER);
        organizationDao.persist(newOrganization);

        return newOrganization.getId();
    }

    @Transactional
    public OrganizationReadDto find(Long id) {
        User user = userService.getSecurityUser();

        Organization foundOrganization = organizationDao.find(id);
        if (foundOrganization == null)
            throw new NotFoundException("Organization not found");

        if(foundOrganization.isUserInOrganization(user.getId()))
            return new OrganizationReadDto(foundOrganization);
        throw new NotFoundException("User is not in organization");

    }


    @Transactional
    public List<OrganizationReadDto> getAll() {
        List<Organization> organizations = organizationDao.findAll();
        return organizations.stream().map(OrganizationReadDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<OrganizationReadDto> getAllByUser() {
        User user = userService.getSecurityUser();
//        userOrgDao
        return  userOrganizationDao.findByUser(user.getId()).stream().map(UserOrganization::getOrganization).map(OrganizationReadDto::new).collect(Collectors.toList());
    }

    @Transactional
    public List<ProjectReadDto> getAllProjects(Long id) {
        Organization organization = organizationDao.find(id);
        if (organization == null)
            throw new NotFoundException("Organization not found");

        User user = userService.getSecurityUser();

        UserOrganization userOrganization = userOrganizationDao.findByUserAndOrg(user.getId(),organization.getId());
        if (userOrganization != null) {
            if (userOrganization.getRole().equals(Role.PROJECT_MANAGER))
                return organization.getProjects().stream().map(ProjectReadDto::new).collect(Collectors.toList());
            else
                return organization.getProjects().stream().filter(project -> project.isUserInProject(user.getId())).map(ProjectReadDto::new).collect(Collectors.toList());
        }
        throw new NotFoundException("User is not in organization");


    }


    @Transactional
    public void update(Long id, OrganizationDto organizationDto) {
        User user = userService.getSecurityUser();

        Organization organization = organizationDao.find(id);
        if (organization == null) throw new NotFoundException("Organization does not exist");

//        jmeno organizace musi byt unikatni
        Organization organizationByName = organizationDao.findByName(organizationDto.getName());
        if (organizationByName != null) throw new AlreadyExistsException("Organization with this name already exists");

        if(organization.isUserPM(user.getId())){
            organization.setName(organizationDto.getName());
            organizationDao.update(organization);
        } throw new NotFoundException("Not enough rights");

    }

    @Transactional
    public void delete(Long id) {
        User user = userService.getSecurityUser();

        Organization organization = organizationDao.find(id);
        if (organization == null) throw new NotFoundException("Organization not found");
        if (!organization.getFounder().equals(user)) throw new NotFoundException("User is not founder");

        organizationDao.remove(organization);
    }

    @Transactional
    public void addUser(Long id, Long userId) {
        Organization organization = organizationDao.find(id);
        if (organization == null)
            throw new NotFoundException("Organization not found");

        User user = userDao.find(userId);
        if (user == null)
            throw new NotFoundException("User not found");

        if(organization.isUserInOrganization(userId)) throw new AlreadyExistsException("User is already in organization");

        User currentUser = userService.getSecurityUser();
        if(!organization.isUserPM(currentUser.getId())) throw new AuthenticationException("You do not have enough rights");

        userOrganizationDao.addUser(organization, user, Role.USER);
    }

//    @Transactional
//    public void addProject(Long id, ProjectDto projectDto, String username) {
//        Organization organization = organizationDao.find(id);
//        if (organization == null)
//            throw new NotFoundException("Organization not found");
//        Project project = projectDao.find(projectService.persist(projectDto, username));
//        organization.getProjects().add(project);
//    }
//
//    @Transactional
//    public void removeProject(Long id, ProjectDto projectDto) {
//        Project project = projectDao.findByName(projectDto.getName());
//        if (project == null)
//            throw new NotFoundException("Project not found");
//        Organization organization = organizationDao.find(id);
//        if (organization == null)
//            throw new NotFoundException("Organization not found");
//        organization.getProjects().remove(project);
//        organizationDao.update(organization);
//        projectDao.remove(project);
//    }

    //TODO fix method
    @Transactional
    public void removeUser(Long id, Long userId) {
        User userToDelete = userDao.find(userId);
        if (userToDelete == null)
            throw new NotFoundException("User not found");

        User currentUser = userService.getSecurityUser();

        Organization organization = organizationDao.find(id);
        if (organization == null)
            throw new NotFoundException("Organization not found");

        UserOrganization userOrganizationToDelete = userOrganizationDao.findByUserAndOrg(userId,id);
        if(userOrganizationToDelete==null)
            throw new NotFoundException("User to delete is not not in organization");

        if(currentUser.equals(userToDelete)||organization.isUserPM(currentUser.getId())){
            userOrganizationDao.remove(userOrganizationToDelete);
            projectUserDao.findByUser(userId).removeIf(projectUser -> projectUser.getProject().getOrganization().equals(organization));
        }else throw new ValidationException("Not enough rights");


    }

    @Transactional
    public void changeRole(Long id, Long userId, String role) {
        User currentUser = userService.getSecurityUser();

        Organization organization = organizationDao.find(id);
        if (organization == null)
            throw new NotFoundException("Organization not found");

        User user = userDao.find(userId);
        if (user == null)
            throw new NotFoundException("User not found");

        UserOrganization userOrganization = userOrganizationDao.findByUserAndOrg(user.getId(),organization.getId());
        if (userOrganization == null)
            throw new NotFoundException("User is not in organization");

        if(!organization.isUserPM(currentUser.getId()))
            throw new NotFoundException("Not enough rights");
        role = role.toLowerCase();
        switch (role) {
            case "user":
                userOrganization.setRole(Role.USER);
                userOrganizationDao.update(userOrganization);
                break;
            case "project manager":
                userOrganization.setRole(Role.PROJECT_MANAGER);
                userOrganizationDao.update(userOrganization);
                break;
            default:
                throw new NotFoundException("Such role is not been allowed");
        }
    }
    @Transactional
    public List<UserReadDto> getUsers(Long id) {
        Organization organization = organizationDao.find(id);
        if (organization == null)
            throw new NotFoundException("Organization not found");
        if(organization.isUserInOrganization(userService.getSecurityUser().getId()))
            return organization.getUserOrganizations().stream().map(UserReadDto::new).collect(Collectors.toList());
        throw new ValidationException("Not enough rights");
    }
}
