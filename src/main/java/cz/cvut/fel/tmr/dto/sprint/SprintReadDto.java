package cz.cvut.fel.tmr.dto.sprint;

import cz.cvut.fel.tmr.model.Sprint;
import cz.cvut.fel.tmr.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SprintReadDto implements Serializable {
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private boolean closed;
    private List<Long> tasks;

    public SprintReadDto(Sprint sprint) {
        id = sprint.getId();
        name = sprint.getName();
        description = sprint.getDescription();
        deadline = sprint.getDeadline();
        closed = sprint.isClosed();
        tasks = sprint.getTasks().stream().map(Task::getId).collect(Collectors.toList());
    }
}
