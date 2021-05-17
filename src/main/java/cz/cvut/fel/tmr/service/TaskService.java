package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.*;
import cz.cvut.fel.tmr.dto.comment.CommentDto;
import cz.cvut.fel.tmr.dto.task.TaskDto;
import cz.cvut.fel.tmr.dto.task.TaskReadDto;
import cz.cvut.fel.tmr.dto.task.TaskTrackedTimeByUserDto;
import cz.cvut.fel.tmr.dto.task.TaskUserDto;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.EarException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.model.*;
import cz.cvut.fel.tmr.model.relations.TaskUser;
import cz.cvut.fel.tmr.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskDao taskDao;
    private final CommentDao commentDao;
    private final ProjectDao projectDao;
    private final UserDao userDao;
    private final TrackedTimeDao trackedTimeDao;
    private final TaskUserDao taskUserDao;
    private final CommentService commentService;
    private final ColumnDao columnDao;
    private final UserOrganizationDao userOrganizationDao;
    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public TaskService(TaskDao taskDao, CommentDao commentDao, ProjectDao projectDao, UserDao userDao, TrackedTimeDao trackedTimeDao, TaskUserDao taskUserDao, CommentService commentService, ColumnDao columnDao, UserOrganizationDao userOrganizationDao, UserService userService) {
        this.taskDao = taskDao;
        this.commentDao = commentDao;
        this.projectDao = projectDao;
        this.userDao = userDao;
        this.taskUserDao = taskUserDao;
        this.commentService = commentService;
        this.trackedTimeDao = trackedTimeDao;
        this.columnDao = columnDao;
        this.userOrganizationDao = userOrganizationDao;
        this.userService = userService;
    }



    /**
     * Adding new task.
     * Project ID required
     * Checks if project for the task exists and if task with same name exists in project.
     * @param taskDto
     */

    @Transactional
    public Long  persist(TaskDto taskDto){
        Objects.requireNonNull(taskDto);

        // required project ID
        if(taskDto.getProjectId() == null) throw new EarException("Project ID required");

        if(taskDto.getColumnId() == null) throw new EarException("TaskColumn ID required");

        // project must exist
        Project foundProject = projectDao.find(taskDto.getProjectId());
        if(foundProject == null) throw new NotFoundException("Project for task not found");

        TaskColumn taskColumn = columnDao.find(taskDto.getColumnId());
        if(taskColumn ==null|| !taskColumn.getProject().getId().equals(foundProject.getId()))
            throw new NotFoundException("TaskColumn for task not found");

        User currentUser = userService.getSecurityUser();
        if(!foundProject.isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        // Task name must be unique
        List<Task> tasks = taskDao.findByProject(taskDto.getProjectId());
        Task sameTask = tasks.stream().filter(t -> t.getName().equals(taskDto.getName())).findFirst().orElse(null);
        if(sameTask != null) throw new AlreadyExistsException("Task, that you want to add already exists");

        Task task = buildFromDto(taskDto);
        taskDao.persist(task);
        return task.getId();
    }

    @Transactional(readOnly = true)
    public List<TaskReadDto> findAll(){
        List<Task> userTasks = taskDao.findByUser(userService.getSecurityUser().getId())
                .stream().map(TaskUser::getTask).collect(Collectors.toList());
        return userTasks.stream().map(TaskReadDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskReadDto find(Long id){
        Task task = taskDao.find(id);
        userService.doesUserHaveEnoughRights(task.getProject());
        return new TaskReadDto(taskDao.find(id));
    }


    @Transactional
    public void update(Long taskId, TaskDto taskDto){
        Objects.requireNonNull(taskDto);
        Task originalTask = taskDao.find(taskId);

        User currentUser = userService.getSecurityUser();
        if(!originalTask.getProject().isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        TaskColumn taskColumn = columnDao.find(taskDto.getColumnId());
        if(taskColumn ==null|| taskColumn.getProject().getId()!=taskDto.getProjectId()) throw new NotFoundException("TaskColumn for task not found");

        Task task = taskDto.updateTask(originalTask);
        task.setTaskColumn(taskColumn);
        taskDao.update(task);
    }

    @Transactional
    public void removeTask(Long taskId){
        Task task = taskDao.find(taskId);

        User currentUser = userService.getSecurityUser();
        if(!task.getProject().isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        task.getProject().getTasks().remove(task);
        projectDao.update(task.getProject());

        Task parentTask = task.getParentTask();
        if(parentTask != null){
            parentTask.getSubtasks().remove(task);
            taskDao.update(parentTask);
        }
        task.setParentTask(null);
        task.setSprint(null);
        task.setProject(null);
        this.taskDao.remove(task);
    }

    @Transactional
    public void addComment(Long taskId, CommentDto commentDto) throws EarException{
        Objects.requireNonNull(taskId);
        Objects.requireNonNull(commentDto);

        Task task = taskDao.find(taskId);

        userService.doesUserHaveEnoughRights(task.getProject());

        Comment comment = commentService.buildFromDto(commentDto);
        comment.setTask(task);
        commentDao.persist(comment);
    }

    @Transactional
    public void removeComment(Long commentId){
        Comment comment = commentDao.find(commentId);
        if(comment == null) throw new NotFoundException("Comment not found");

        User currentUser = userService.getSecurityUser();
        if(!currentUser.equals(comment.getUser()) ||
                !comment.getTask().getProject().getOrganization().isUserPM(currentUser.getId()))
            throw new NotFoundException("User has no rights remove comment");
        commentDao.remove(comment);
    }

    @Transactional
    public void updateComment(Long commentId, CommentDto dto){
        Comment comment = commentDao.find(commentId);
        if(comment == null) throw new NotFoundException("Comment not found");
        User currentUser = userService.getSecurityUser();
        if(!currentUser.equals(comment.getUser()))
            throw new NotFoundException("User has no rights to update comment");
        comment = dto.update(comment);
        commentDao.update(comment);
    }

    @Transactional
    public void addAssignee(TaskUserDto dto){
        Objects.requireNonNull(dto);
        Task task = taskDao.find(dto.getTaskId());
        userService.doesUserHaveEnoughRights(task.getProject());
        TaskUser taskUser = buildFromDto(dto);
        taskUserDao.persist(taskUser);
    }


    @Transactional
    public void removeAssignee(Long taskId, Long userId){
        Task task = taskDao.find(taskId);
        userService.doesUserHaveEnoughRights(task.getProject());
        TaskUser taskUser = taskUserDao.find(taskId, userId);
        taskUserDao.remove(taskUser);

    }

    @Transactional
    public void addSubtask(Long parentTaskId, TaskDto subtaskDto){
        Objects.requireNonNull(parentTaskId);
        Objects.requireNonNull(subtaskDto);
        Task parentTask = taskDao.find(parentTaskId);

        User currentUser = userService.getSecurityUser();
        if(!parentTask.getProject().isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        subtaskDto.setProjectId(parentTask.getProject().getId());

        Task childTask = buildFromDto(subtaskDto);
        parentTask.addSubtask(childTask);
        childTask.setParentTask(parentTask);
        taskDao.persist(childTask);
    }

    @Transactional
    public List<Task> findByProject(Long projectId){
        Objects.requireNonNull(projectId);
        userService.doesUserHaveEnoughRights(projectDao.find(projectId));
        return taskDao.findByProject(projectId);
    }

    @Transactional
    public List<Task> findByColumn(Long columnId){
        Objects.requireNonNull(columnId);
        TaskColumn column = columnDao.find(columnId);
        userService.doesUserHaveEnoughRights(column.getProject());
        return taskDao.findByColumn(columnId);
    }

    // rest to entity
    private Task buildFromDto(TaskDto dto){
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setDeadline(dto.getDeadline());
        task.setProject(projectDao.find(dto.getProjectId()));
        task.setTaskColumn(columnDao.find(dto.getColumnId()));
        return task;
    }

    private TaskUser buildFromDto(TaskUserDto dto){
        Task task = taskDao.find(dto.getTaskId());
        if(task == null) throw new NotFoundException("Task was not found");
        User user = userDao.find(dto.getUserId());
        if(user == null) throw new NotFoundException("User was not found");


        TaskUser taskUser = new TaskUser();
        taskUser.setTask(task);
        taskUser.setUser(user);
        taskUser.setCreated(dto.getCreated());
        return taskUser;
    }

}
