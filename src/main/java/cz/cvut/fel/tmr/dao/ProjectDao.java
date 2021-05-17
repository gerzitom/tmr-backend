package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Project;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDao extends BaseDao<Project> {

    public ProjectDao() {
        super(Project.class);
    }

    public Project findByName(String projectName){
        try{
            Project foundProject = em.createNamedQuery("Project.findByName", Project.class)
                    .setParameter("project", projectName)
                    .getSingleResult();
            return foundProject;
        } catch (Exception e){
            return null;
        }
    }
}
