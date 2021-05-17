package cz.cvut.fel.tmr.model.relations;


import cz.cvut.fel.tmr.model.AbstractEntity;
import cz.cvut.fel.tmr.model.Organization;
import cz.cvut.fel.tmr.model.Role;
import cz.cvut.fel.tmr.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "UserOrganization.findByUserAndOrg", query = "SELECT u from UserOrganization u WHERE :user = u.user.id and :organization = u.organization.id"),
        @NamedQuery(name = "UserOrganization.findByUser", query = "SELECT u from UserOrganization u WHERE :user = u.user.id")
})
public class UserOrganization extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "organization")
    Organization organization;

    @Enumerated(EnumType.STRING)
    private Role role;

    public UserOrganization(User user, Organization organization) {
        this.user = user;
        this.organization = organization;
    }
}
