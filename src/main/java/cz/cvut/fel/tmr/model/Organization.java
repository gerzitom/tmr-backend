package cz.cvut.fel.tmr.model;

import cz.cvut.fel.tmr.model.relations.ProjectUser;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Organization.findByName", query = "SELECT o from Organization o WHERE :organization = o.name"),
})
public class Organization extends AbstractEntity{

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    private User founder;

    @OneToMany(mappedBy = "organization")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<UserOrganization> userOrganizations = new ArrayList<>();

    @OneToMany(mappedBy="organization")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<Project> projects = new ArrayList<>();

    public Organization(String name) {
        this.name = name;
    }

    public Organization(String name, User founder) {
        this.name = name;
        this.founder = founder;
    }
    public boolean isUserInOrganization(Long userId){
        return userOrganizations.stream().anyMatch(userOrganization -> userOrganization.getUser().getId().equals(userId));
    }

    public boolean isUserPM(Long userId){
        return userOrganizations.stream().anyMatch(userOrganization -> userOrganization.getUser().getId().equals(userId) && userOrganization.getRole().equals(Role.PROJECT_MANAGER));
    }

}
