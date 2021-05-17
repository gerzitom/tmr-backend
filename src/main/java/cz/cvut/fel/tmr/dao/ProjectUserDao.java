package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.User;
import cz.cvut.fel.tmr.model.relations.ProjectUser;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProjectUserDao extends BaseDao<ProjectUser>  {
    public ProjectUserDao() {
        super(ProjectUser.class);
    }

    public ProjectUser find(Long projectId, Long userId){
        return em.createNamedQuery("ProjectUser.find", ProjectUser.class)
                .setParameter("projectId", projectId)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    public List<ProjectUser> findByUser(Long userId){
        return em.createNamedQuery("ProjectUser.findByUser", ProjectUser.class)
                .setParameter("user", userId)
                .getResultList();
    }

    public ProjectUser findByUserAndProject(Long userId,Long projectId){
        return em.createNamedQuery("ProjectUser.findByUserAndProject", ProjectUser.class)
                .setParameter("user", userId)
                .setParameter("project", projectId)
                .getResultList().stream().findFirst().orElse(null);
    }

    public void addUser(Project project, User user){
        ProjectUser projectUser = new ProjectUser(user, project);
        projectUser.setAdded(LocalDateTime.now());
        persist(projectUser);
    }

}
