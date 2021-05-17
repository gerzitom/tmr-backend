package cz.cvut.fel.tmr.model.relations;

import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.Role;
import cz.cvut.fel.tmr.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "ProjectUser.find", query = "SELECT u from ProjectUser u WHERE :projectId = u.project.id AND :userId = u.user.id"),
        @NamedQuery(name = "ProjectUser.findByUser", query = "SELECT u from ProjectUser u WHERE :user = u.user.id"),
        @NamedQuery(name = "ProjectUser.findByUserAndProject  ", query = "SELECT u from ProjectUser u WHERE :user = u.user.id and :project = u.project.id")
})
public class ProjectUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    Project project;

    private LocalDateTime added;

    public ProjectUser(User user, Project project) {
        this.user = user;
        this.project = project;
        this.added = LocalDateTime.now();
    }
}
