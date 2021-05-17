package cz.cvut.fel.tmr.model;

import cz.cvut.fel.tmr.model.relations.ProjectUser;
import cz.cvut.fel.tmr.model.relations.TaskUser;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "User", description = "User of the project", oneOf = User.class)
@Table(name = "EAR_USER")
@NamedQueries({
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
})
public class User extends AbstractEntity{

//    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private String username;

    private String name;

    private String password;

    private boolean removed;

    @ManyToOne
    private Image avatar;

    @OneToMany(mappedBy = "responsibleUser")
    private List<Task> responsibleForTasks;

    @OneToMany(mappedBy = "user")
    private List<TaskUser> taskUsers;

    @OneToMany(mappedBy = "user")
    private Set<ProjectUser> projectUsers;

    @OneToMany(mappedBy = "user")
    private List<UserOrganization> userOrganizations;


    public User(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public boolean isRemoved(){
        return removed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
