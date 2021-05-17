package cz.cvut.fel.tmr.dto.organization;

import cz.cvut.fel.tmr.dto.project.ProjectReadDto;
import cz.cvut.fel.tmr.dto.user.UserReadDto;
import cz.cvut.fel.tmr.model.Organization;
import cz.cvut.fel.tmr.model.relations.UserOrganization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
@AllArgsConstructor
public class OrganizationReadDto implements Serializable {
    private Long id;
    private String name;
    private UserReadDto founder;
    private List<UserReadDto> users;
    private List<ProjectReadDto> projects;

    public OrganizationReadDto() {
    }

    public OrganizationReadDto(String name) {
        this.name = name;
    }

    public OrganizationReadDto(Organization organization) {
        this.id = organization.getId();
        this.name = organization.getName();
        this.founder = new UserReadDto(organization.getFounder());
        this.users = organization.getUserOrganizations().stream().map(UserOrganization::getUser).map(UserReadDto::new).collect(Collectors.toList());
        this.projects = organization.getProjects().stream().map(ProjectReadDto::new).collect(Collectors.toList());
    }
}
