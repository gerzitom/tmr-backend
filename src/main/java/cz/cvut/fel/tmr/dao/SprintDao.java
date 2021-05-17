package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Sprint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SprintDao extends BaseDao<Sprint> {
    public SprintDao() {
        super(Sprint.class);
    }

    public Sprint getCurrentSprint(Long projectId){
        try{
            return em.createNamedQuery("Sprint.getCurrentSprint", Sprint.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (Exception e){
            return null;
        }
    }

    public List<Sprint> getSprints(Long projectId) {
        try{
            return em.createNamedQuery("Sprint.getSprints", Sprint.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
        } catch (Exception e){
            return null;
        }
    }
}
