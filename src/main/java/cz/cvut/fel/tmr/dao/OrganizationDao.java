package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Organization;
import cz.cvut.fel.tmr.model.Project;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrganizationDao extends BaseDao<Organization> {
    public OrganizationDao() {
        super(Organization.class);
    }

    public Organization findByName(String name) {
        try{
            Organization foundOrganization = em.createNamedQuery("Organization.findByName", Organization.class)
                    .setParameter("organization", name)
                    .getSingleResult();
            return foundOrganization;
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public Organization update(Organization entity) {
        System.out.println("update");
        return super.update(entity);
    }
}
