package cz.cvut.fel.tmr.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Schema(name = "Sprint", description = "Sprint", oneOf = Sprint.class)
@NamedQueries({
        @NamedQuery(name = "Sprint.getCurrentSprint", query = "SELECT s FROM Sprint s WHERE :projectId = s.project.id AND s.closed = false"),
        @NamedQuery(name = "Sprint.getSprints", query = "SELECT s FROM Sprint s WHERE :projectId = s.project.id")}
)
public class Sprint extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

//    @NotNull
    private String name;

    private String description;

//    @NotNull
    private LocalDate deadline;

    private boolean closed;

    @OneToMany(mappedBy = "sprint")
    private List<Task> tasks;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sprint)) return false;
        Sprint sprint = (Sprint) o;
        return getProject().equals(sprint.getProject()) && getName().equals(sprint.getName()) && getDeadline().equals(sprint.getDeadline());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject(), getName(), getDeadline());
    }

}
