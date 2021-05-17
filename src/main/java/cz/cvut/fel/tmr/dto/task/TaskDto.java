package cz.cvut.fel.tmr.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tmr.model.State;
import cz.cvut.fel.tmr.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto implements Serializable {
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private State state;
    private Long projectId;
    private Long columnId;
    private List<Long> users;

    public TaskDto(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.deadline = task.getDeadline();
        this.projectId = task.getProject().getId();
        this.columnId = task.getTaskColumn().getId();
        this.state = task.getState();
    }

    public Task updateTask(Task task){
        if(this.name != null) task.setName(this.name);
        if(this.description != null) task.setDescription(this.description);
        if(this.deadline != null) task.setDeadline(this.deadline);
        if(this.state != null) task.setState(this.state);
        return task;
    }
}
