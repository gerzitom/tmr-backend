package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.TmApplication;
import cz.cvut.fel.tmr.exception.PersistenceException;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.State;
import cz.cvut.fel.tmr.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static cz.cvut.fel.tmr.environment.Generator.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan(basePackageClasses = TmApplication.class)
class TaskDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private TaskDao dao;

    @Autowired
    private ProjectDao projectDao;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void persistSavesSpecifiedInstance() {
        final Task task = generateTask();
        dao.persist(task);
        assertNotNull(task.getId());

        final Task result = em.find(Task.class, task.getId());
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getName(), result.getName());
    }

    @Test
    public void findRetrievesInstanceByIdentifier() {
        final Task task = generateTask();
        em.persistAndFlush(task);
        assertNotNull(task.getId());

        final Task result = dao.find(task.getId());
        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getName(), result.getName());
    }

    @Test
    public void findAllRetrievesAllInstancesOfType() {
        final Task taskOne = generateTask();
        em.persistAndFlush(taskOne);
        final Task taskTwo = generateTask();
        em.persistAndFlush(taskTwo);

        final List<Task> result = dao.findAll();
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(taskOne.getId())));
        assertTrue(result.stream().anyMatch(c -> c.getId().equals(taskTwo.getId())));
    }

    @Test
    public void updateUpdatesExistingInstance() {
        final Task task = generateTask();
        em.persistAndFlush(task);

        final Task update = new Task();
        update.setId(task.getId());
        final String newName = "New Task name";
        update.setName(newName);
        dao.update(update);

        final Task result = dao.find(task.getId());
        assertNotNull(result);
        assertEquals(task.getName(), result.getName());
    }

    @Test
    public void removeRemovesSpecifiedInstance() {
        final Task task = generateTask();
        em.persistAndFlush(task);
        assertNotNull(em.find(Task.class, task.getId()));
        em.detach(task);

        dao.remove(task);
        assertNull(em.find(Task.class, task.getId()));
    }

    @Test
    public void removeDoesNothingWhenInstanceDoesNotExist() {
        final Task task = generateTask();
        task.setId(123);
        assertNull(em.find(Task.class, task.getId()));

        dao.remove(task);
        assertNull(em.find(Task.class, task.getId()));
    }

    @Test
    public void exceptionOnPersistInWrappedInPersistenceException() {
        assertThrows(PersistenceException.class, () -> {
            final Task task = generateTask();
            em.persistAndFlush(task);
            em.remove(task);
            dao.update(task);
        });
    }

    @Test
    public void existsReturnsTrueForExistingIdentifier() {
        final Task task = generateTask();
        em.persistAndFlush(task);
        assertTrue(dao.exists(task.getId()));
        assertFalse(dao.exists((long)-1));
    }

    @Test
    public void findByProject_findsTasksByProject(){
        final Task taskInProject = generateTask();
        final Task taskNotInProject = generateTask();
        final Project project = generateProject();
        project.addTask(taskInProject);

        projectDao.persist(project);
        em.persistAndFlush(taskNotInProject);

        List<Task> foundTasks = dao.findByProject(project.getId());
        assertTrue(foundTasks.contains(taskInProject));
    }

    @Test
    public void findDoneTasks_findsTasksOfProjectWhenDone(){
        Project project = generateProject();
        Task doneTask = generateTask();
        doneTask.setState(State.DONE);

        Task inProgressTask = generateTask();
        inProgressTask.setState(State.IN_PROGRESS);

        project.addTask(doneTask);
        project.addTask(inProgressTask);

        projectDao.persist(project);

        List<Task> projectTasks = dao.findDoneTasks(project.getId());
        assertTrue(projectTasks.contains(doneTask));
    }
}