package cz.cvut.fel.tmr.dto.project;

import cz.cvut.fel.tmr.dto.sprint.SprintReadDto;
import cz.cvut.fel.tmr.model.Project;
import cz.cvut.fel.tmr.model.Sprint;
import cz.cvut.fel.tmr.model.User;
import cz.cvut.fel.tmr.model.relations.ProjectUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectReadDto implements Serializable {
    private Long id;
    private Long organizationId;
    private String name;
    private LocalDate deadline;
    private List<Long> users;
    private SprintReadDto currentSprint;

    public ProjectReadDto(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.deadline = project.getDeadline();
        this.organizationId = project.getOrganization().getId();
        this.users = project.getProjectUsers().stream().map(ProjectUser::getUser).map(User::getId).collect(Collectors.toList());
        List<Sprint> sprints = project.getSprints().stream().filter(sprint -> !sprint.isClosed()).collect(Collectors.toList());
        if(sprints.size() > 0) this.currentSprint = new SprintReadDto(sprints.get(0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectReadDto)) return false;
        ProjectReadDto that = (ProjectReadDto) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(deadline, that.deadline) &&
                Objects.equals(organizationId, that.organizationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deadline);
    }
}
