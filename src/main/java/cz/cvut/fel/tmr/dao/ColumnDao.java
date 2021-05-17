package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.TaskColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ColumnDao extends  BaseDao<TaskColumn> {
    public ColumnDao() {
        super(TaskColumn.class);
    }

    public List<TaskColumn> findByProject(Long projectId) {
        return em.createNamedQuery("Column.findByProject", TaskColumn.class)
                .setParameter("project", projectId)
                .getResultList();
    }


    public TaskColumn findByNameAndProject(String columnName, Long projectId){
        try{
            return em.createNamedQuery("Column.findByNameAndProject", TaskColumn.class)
                    .setParameter("projectId", projectId)
                    .setParameter("column", columnName)
                    .getSingleResult();
        } catch (Exception e){
            return null;
        }
    }

}
