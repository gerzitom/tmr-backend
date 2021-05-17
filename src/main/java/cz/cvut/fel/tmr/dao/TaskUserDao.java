package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.relations.TaskUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskUserDao extends BaseDao<TaskUser> {

    public TaskUserDao() {
        super(TaskUser.class);
    }

    public TaskUser find(Long taskId, Long userId){
        return em.createNamedQuery("TaskUser.find", TaskUser.class)
                .setParameter("taskId", taskId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public List<TaskUser> getUpcommingTasks(Long userId){
        return em.createNamedQuery("TaskUser.findUpcommingTasks", TaskUser.class)
                .setParameter("userId", userId)
                .getResultList();
    }

}
