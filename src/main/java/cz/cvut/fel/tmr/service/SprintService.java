package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.ProjectDao;
import cz.cvut.fel.tmr.dao.SprintDao;
import cz.cvut.fel.tmr.dao.TaskDao;
import cz.cvut.fel.tmr.dao.UserOrganizationDao;
import cz.cvut.fel.tmr.dto.sprint.SprintDto;
import cz.cvut.fel.tmr.dto.sprint.SprintReadDto;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.exception.UnauthorizedException;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.Sprint;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SprintService {

    private SprintDao dao;
    private ProjectDao projectDao;
    private TaskDao taskDao;

    private UserService userService;
    private UserOrganizationDao userOrganizationDao;


    public SprintService(SprintDao dao, ProjectDao projectDao, TaskDao taskDao, UserService userService) {
        this.dao = dao;
        this.projectDao = projectDao;
        this.taskDao = taskDao;
        this.userService = userService;
    }

    private boolean doesUserHaveEnoughRights(User user, Project project) {
        return project.getOrganization().isUserPM(user.getId());
    }

    //TODO check that projectId is not empty
    @Transactional
    public Long persist(SprintDto dto) {
        Objects.requireNonNull(dto);

        User user = userService.getSecurityUser();
        Project project = projectDao.find(dto.getProjectId());

        if (project == null)
            throw new NotFoundException("Project does not exist");

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        for (Sprint sprint : project.getSprints()) {
            if (!sprint.isClosed()) throw new AlreadyExistsException("That project already has an active sprint");
        }

        Sprint sprint = buildFromDto(dto);
        dao.persist(sprint);
        return sprint.getId();
    }

    @Transactional
    public SprintReadDto find(Long sprintId) {
        User user = userService.getSecurityUser();
        Sprint sprint = dao.find(sprintId);
        if (sprint == null)
            throw new NotFoundException("Sprint was not found");
        Project project = sprint.getProject();

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        return new SprintReadDto(sprint);
    }

    //TODO the problem that not admin can learn if sprint exist or not
    @Transactional
    public void update(Long sprintId, SprintDto dto) {
        Objects.requireNonNull(dto);
        Sprint sprint = dao.find(sprintId);
        User user = userService.getSecurityUser();

        if (sprint == null)
            throw new NotFoundException("Sprint was not found");

        Project project = projectDao.find(sprint.getProject().getId());

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        sprint = dto.update(sprint);
        dao.update(sprint);
    }

    @Transactional
    public void delete(Long sprintId) {
        Sprint sprint = dao.find(sprintId);
        if (sprint == null)
            throw new NotFoundException("Sprint was not found");
        User user = userService.getSecurityUser();
        Project project = sprint.getProject();

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        dao.remove(sprint);
    }

    @Transactional
    public SprintReadDto findCurrentSprint(Long projectId) {
        User user = userService.getSecurityUser();
        Project project = projectDao.find(projectId);

        if (project == null) throw new NotFoundException("Project not found");

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        Sprint sprint = getCurrentSprint(projectId);

        return new SprintReadDto(getCurrentSprint(projectId));
    }

    @Transactional
    public List<SprintReadDto> findSprints(Long projectId) {
        User user = userService.getSecurityUser();
        Project project = projectDao.find(projectId);

        if (project == null) throw new NotFoundException("Project not found");

        if (!doesUserHaveEnoughRights(user, project))
            throw new UnauthorizedException("Not enough rights");

        List<Sprint> sprints = dao.getSprints(projectId);
        List<SprintReadDto> sprintReadDtos = new ArrayList();
        for (Sprint sprint : sprints)
            sprintReadDtos.add(new SprintReadDto(sprint));

        return sprintReadDtos;
    }

    @Transactional
    public void addTask(Long sprintId, Long taskId) {
        Task task = taskDao.find(taskId);
        if (task == null)
            throw new NotFoundException("Task does not exist");

        Sprint sprint = dao.find(sprintId);
        if (sprint == null)
            throw new NotFoundException("Sprint does not exist");

        if (sprint.getTasks().contains(task))
            throw new AlreadyExistsException("This sprint already contains that task");

        User user = userService.getSecurityUser();
        if (!sprint.getProject().getOrganization().isUserPM(user.getId()))
            throw new UnauthorizedException("Not enough rights");

        task.setSprint(sprint);
        taskDao.update(task);
    }


    @Transactional
    public void removeTask(Long sprintId, Long taskId) {
        Task task = taskDao.find(taskId);
        if (task == null)
            throw new NotFoundException("Task does not exist");

        Sprint sprint = dao.find(sprintId);
        if (sprint == null)
            throw new NotFoundException("Sprint does not exist");

        if (sprint.getTasks().stream().noneMatch(t -> t.equals(task)))
            throw new NotFoundException("This sprint does not have this task");

        User user = userService.getSecurityUser();
        if (!sprint.getProject().getOrganization().isUserPM(user.getId()))
            throw new UnauthorizedException("Not enough rights");

        sprint.getTasks().remove(task);
        task.setSprint(null);

        taskDao.update(task);
        dao.update(sprint);
    }

    private Sprint getCurrentSprint(Long projectId) {
        Project project = projectDao.find(projectId);

        if (project == null)
            throw new NotFoundException("Project not found");

        Sprint sprint = dao.getCurrentSprint(projectId);

        if (sprint != null)
            return sprint;
        else
            throw new NotFoundException("Sprint not found");
    }

    private Sprint buildFromDto(SprintDto dto) {
        Sprint sprint = new Sprint();
        sprint.setName(dto.getName());
        sprint.setDeadline(dto.getDeadline());
        sprint.setDescription(dto.getDescription());
        sprint.setProject(projectDao.find(dto.getProjectId()));
        sprint.setClosed(false);
        return sprint;
    }
}
