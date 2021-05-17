package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Organization;
import cz.cvut.fel.tmr.model.Role;
import cz.cvut.fel.tmr.model.User;
import cz.cvut.fel.tmr.model.relations.ProjectUser;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserOrganizationDao extends BaseDao<UserOrganization>{
    public UserOrganizationDao() {
        super(UserOrganization.class);
    }


    public void addUser(Organization organization, User user, Role role){
        UserOrganization userOrganization = new UserOrganization(user,organization);
        userOrganization.setRole(role);
        persist(userOrganization);
    }
    public UserOrganization findByUserAndOrg(Long userId,Long orgId){
        return em.createNamedQuery("UserOrganization.findByUserAndOrg", UserOrganization.class)
                .setParameter("user", userId)
                .setParameter("organization",orgId)
                .getResultList().stream().findFirst().orElse(null);
    }

    public List<UserOrganization> findByUser(Long id) {
        return em.createNamedQuery("UserOrganization.findByUser", UserOrganization.class)
                .setParameter("user", id)
                .getResultList();
    }
}
