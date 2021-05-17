package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.State;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.relations.TaskUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskDao extends BaseDao<Task> {
    public TaskDao() {
        super(Task.class);
    }

    public List<Task> findByProject(Long projectId) {
        return em.createNamedQuery("Task.findByProject", Task.class)
                .setParameter("project", projectId)
                .getResultList();
    }

    public List<TaskUser> findByUser(Long userId) {
        return em.createNamedQuery("Task.findByUser", TaskUser.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Task> findDoneTasks(Long projectId){
        return em.createNamedQuery("Task.findByProjectAndState", Task.class)
                .setParameter("project", projectId)
                .setParameter("state", State.DONE)
                .getResultList();
    }

    public List<Task> findByColumn(Long columnId) {
        return em.createNamedQuery("Task.findByColumn", Task.class)
                .setParameter("column", columnId)
                .getResultList();
    }
}
