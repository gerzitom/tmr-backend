package cz.cvut.fel.tmr.dto.organization;

import cz.cvut.fel.tmr.model.Organization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationDto implements Serializable {
    private String name;

    public OrganizationDto(String name) {
        this.name = name;
    }

    public OrganizationDto() {
    }

    public OrganizationDto(Organization foundOrganization) {
        this.name = foundOrganization.getName();
    }

    public String getName() {
        return name;
    }

    public Organization buildNewOrganization() {
        Organization organization = new Organization(name);
        return organization;
    }
}
