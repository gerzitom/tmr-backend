//package cz.cvut.fel.tmr.service;
//
//import cz.cvut.fel.tmr.dto.project.ProjectDto;
//import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
//import cz.cvut.fel.tmr.environment.Generator;
//import cz.cvut.fel.tmr.exception.AlreadyExistsException;
//import cz.cvut.fel.tmr.exception.AuthenticationException;
//import cz.cvut.fel.tmr.exception.NotFoundException;
//import cz.cvut.fel.tmr.model.Project;
//import cz.cvut.fel.tmr.model.SecurityUser;
//import cz.cvut.fel.tmr.model.User;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//@TestPropertySource(locations = "classpath:application-test.properties")
//class ProjectServiceTest {
//
//    private String rawToken;
//    private SecurityUser securityUser;
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private MyUserDetailsService userDetailsService;
//
//    @BeforeEach
//    void setUp() {
//        User user = Generator.generateUser();
//        em.persist(user);
//        securityUser = new SecurityUser(user);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//        rawToken = Generator.generateRawAuthenticationToken(userDetails);
//    }
//
//    // Basic CRUD Operations
//
//    @Test
//    public void persistProject__projectIsAddedToDatabase(){
//        // persist new project
//        ProjectDto project = Generator.generateProjectDto();
//        Long newProjectId = projectService.persist(project, rawToken);
//        Project newProject = project.buildNewProject();
//        newProject.setId(newProjectId);
//
//        // find project
//        Project foundProject = em.find(Project.class, newProjectId);
//        assertEquals(newProject, foundProject);
//    }
//
//    @Test
//    public void findProject__projectCanBeFound(){
//        Project project = Generator.generateProject();
//        em.persist(project);
//
//        // find project
//        ProjectReadDto foundProject = projectService.find(project.getId());
//
//        assertEquals(new ProjectReadDto(project), foundProject);
//    }
//
//    @Test
//    public void findProjectByUsername__findsProject(){
//        ProjectDto project = Generator.generateProjectDto();
//        projectService.persist(project, rawToken);
//
//        ProjectReadDto foundProject = projectService.findByName(project.getName());
//        assertEquals(foundProject, foundProject);
//    }
//
//    @Test
//    public void updateProject__updatesProject(){
//        // persist new project
//        String updatedName = "Persisted project updated name";
//        ProjectDto projectDto = Generator.generateProjectDto();
//        Long persistedProject = projectService.persist(projectDto, rawToken);
//
//        // update project
//        ProjectDto projectDtoUpdated = new ProjectDto();
//        projectDtoUpdated.setName(updatedName);
//        projectService.update(persistedProject, projectDtoUpdated);
//
//        // assert
//        ProjectReadDto updatedProject = projectService.find(persistedProject);
//        assertEquals(updatedName, updatedProject.getName());
//    }
//
//    @Test
//    public void addProjectWithSameName__throwsAlreadyExistsException(){
//        String projectName = "Project name";
//        ProjectDto projectDto = Generator.generateProjectDto();
//        projectDto.setName(projectName);
//        projectService.persist(projectDto, rawToken);
//
//        assertThrows(AlreadyExistsException.class, () -> {
//            ProjectDto nextProjectDto = Generator.generateProjectDto();
//            nextProjectDto.setName(projectName);
//            projectService.persist(nextProjectDto, rawToken);
//        });
//    }
//
//    @Test
//    public void getProject_throwsNotFoundException(){
//        assertThrows(NotFoundException.class, () -> {
//            Project project = Generator.generateProject();
//            em.persist(project);
//
//            projectService.find((long)2);
//        });
//    }
//
//    @Test
//    public void getAll_throwsIsRemovedException(){
//        //generating User
//        User user = Generator.generateUser();
//        em.persist(user);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//        String rawToken1 = Generator.generateRawAuthenticationToken(userDetails);
//
//        //User is removed
//        user.setRemoved(true);
//        em.merge(user);
//
//        assertThrows(AuthenticationException.class, () -> {
//            Project project = Generator.generateProject();
//            em.persist(project);
//
//            projectService.getAll(securityUser);
//        });
//    }
//
//}