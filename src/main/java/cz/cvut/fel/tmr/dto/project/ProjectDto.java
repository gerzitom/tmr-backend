package cz.cvut.fel.tmr.dto.project;

import cz.cvut.fel.tmr.model.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto implements Serializable {
    private String name;
    private LocalDate deadline;
    private Long organizationId;

    public Project updateProject(Project project) {
        if (this.name != null) project.setName(this.name);
        if (this.deadline != null) project.setDeadline(this.deadline);
        return project;
    }

    public Project buildNewProject() {
        Project project = new Project();
        project.setName(this.name);
        project.setDeadline(this.deadline);
        return project;
    }
}
