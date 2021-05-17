//package cz.cvut.fel.tmr.service;
//
//
//import cz.cvut.fel.tmr.dto.comment.CommentDto;
//import cz.cvut.fel.tmr.environment.Generator;
//import cz.cvut.fel.tmr.exception.EarException;
//import cz.cvut.fel.tmr.model.Project;
//import cz.cvut.fel.tmr.model.SecurityUser;
//import cz.cvut.fel.tmr.model.Task;
//import cz.cvut.fel.tmr.model.User;
//import cz.cvut.fel.tmr.model.relations.TaskUser;
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
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static java.lang.Math.abs;
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//@TestPropertySource(locations = "classpath:application-test.properties")
//class TaskServiceTest {
//
//    private User user;
//    private SecurityUser securityUser;
//
//    @PersistenceContext
//    EntityManager em;
//
//    @Autowired
//    private TaskService sut;
//
//    @Autowired
//    private MyUserDetailsService userDetailsService;
//
//    @BeforeEach
//    void setUp() {
//        user = Generator.generateUser();
//        em.persist(user);
//        securityUser = new SecurityUser(user);
//    }
//
//
//
//    @Test
//    public void addSubtaskAddsTaskToProject(){
//        Task task = Generator.generateTask();
//
//        Project project = new Project();
//        em.persist(project);
//
//        task.setProject(project);
//        em.persist(task);
//
//        List<Task> tasks = sut.findByProject(project.getId()).stream().filter(e -> e.getId().equals(task.getId())).collect(Collectors.toList());
//
//        assertNotNull(tasks);
//    }
//
//
//    @Test
//    public void addCommentToTaskFromNonMemberThrowsException(){
//        //persist new task
//        Task task = Generator.generateTask();
//        em.persist(task);
//
//        //Creating new comment
//        CommentDto commentDto = Generator.generateCommentDto();
//
//        assertThrows(EarException.class, () -> sut.addComment(task.getId(), commentDto,securityUser));
//    }
//
//    @Test
//    public void addCommentToTaskFromMemberDoesNotThrow(){
//
//        //persist new task
//        Task task = Generator.generateTask();
//        User user = Generator.generateUser();
//        em.persist(user);
//        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//        String rawToken1 = Generator.generateRawAuthenticationToken(userDetails);
//
//        TaskUser taskUser = new TaskUser();
//        taskUser.setUser(user);
//        task.getTaskUsers().add(taskUser);
//        em.persist(task);
//
//        //Creating new comment
//        CommentDto commentDto = Generator.generateCommentDto();
//        commentDto.setUserId(user.getId());
//        sut.addComment(task.getId(), commentDto, securityUser);
//
//
//        assertDoesNotThrow(() -> sut.addComment(task.getId(), commentDto, securityUser));
//    }
//}

