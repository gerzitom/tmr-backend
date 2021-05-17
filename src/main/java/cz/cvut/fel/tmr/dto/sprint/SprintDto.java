package cz.cvut.fel.tmr.dto.sprint;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tmr.model.Sprint;
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
public class SprintDto implements Serializable {
    private Long projectId;
    private String name;
    private String description;
    private boolean closed;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    public Sprint update(Sprint sprint) {
        sprint.setClosed(this.closed);
        if (this.getName() != null) sprint.setName(this.name);
        if (this.getDescription() != null) sprint.setDescription(this.description);
        if (this.getDeadline() != null) sprint.setDeadline(this.deadline);
        return sprint;
    }
}
