package cz.cvut.fel.tmr.model;

import cz.cvut.fel.tmr.model.relations.ProjectUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Entity(name = "Project")
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Project", description = "Project", oneOf = Project.class)
@NamedQueries({
        @NamedQuery(name = "Project.findByName", query = "SELECT t from Project t WHERE :project = t.name"),
        @NamedQuery(name = "Project.getTasksBy", query = "SELECT t from Task t WHERE :project = t.project.id AND t.state = :state"),
//        @NamedQuery(name = "Project.findByOrganization", query = "SELECT p from Project p WHERE :organization = p.organization.id"),
})
public class Project{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", updatable = false, nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    private LocalDate deadline;

    @OneToMany(
            mappedBy = "project",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<Sprint> sprints = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    private List<ProjectUser> projectUsers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private State state = State.IN_PROGRESS;

    @OneToMany(mappedBy = "project")
    private List<TaskColumn> taskColumns = new ArrayList<>();

    @ManyToOne
    private Organization organization;

    public Project(String name) {
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.deadline = LocalDate.now();
    }

    public Project(String name, LocalDate deadline) {
        this.name = name;
        this.deadline = deadline;
    }

    public void addTask(Task task){
        this.tasks.add(task);
        // TODO can this be solved by some cascade
//        task.setProject(this);
    }

    public boolean taskExists(String taskName){
        AtomicReference<Boolean> taskExists = new AtomicReference<>(false);
        this.tasks.forEach(task -> {
            if(task.getName().equals(taskName)){
                taskExists.set(true);
            }
        });
        return taskExists.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id.equals(project.id) &&
                name.equals(project.name);
    }

    public boolean isUserInProject(Long userId){
        return projectUsers.stream().anyMatch(projectUser -> projectUser.getUser().getId().equals(userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
