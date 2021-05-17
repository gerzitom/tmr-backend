package cz.cvut.fel.tmr.environment;

import cz.cvut.fel.tmr.dto.comment.CommentDto;
import cz.cvut.fel.tmr.dto.project.ProjectDto;
import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeDto;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.User;
import cz.cvut.fel.tmr.util.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setName("Test name " + randomInt());
        user.setUsername("username" + randomInt() + "@fel.cvut.cz");
        user.setPassword(Integer.toString(randomInt()));
        return user;
    }

    public static Task generateTask(){
        Task task = new Task();
        task.setName("Testing task " + randomInt());
        task.setDescription("Testing task description");
        return task;
    }

    public static Project generateProject(){
        Project project = new Project();
        project.setName("Project " + randomInt());
        return project;
    }

    public static ProjectDto generateProjectDto(){
        ProjectDto projectDto = new ProjectDto();
        projectDto.setName("Project " + randomInt());
        return projectDto;
    }

    public static CommentDto generateCommentDto(){
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Comment " + randomInt());
        return commentDto;
    }

    public static TrackedTimeDto generateTrackedTimeDto(){
        TrackedTimeDto trackedTimeDto = new TrackedTimeDto();
        trackedTimeDto.setStartTime(LocalDateTime.now());
        return trackedTimeDto;
    }

    public static String generateRawAuthenticationToken(UserDetails userDetails){
        JwtUtil util = new JwtUtil();
        return "Bearer " + util.generateToken(userDetails);
    }
}
