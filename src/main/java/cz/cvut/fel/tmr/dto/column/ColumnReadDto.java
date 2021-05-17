package cz.cvut.fel.tmr.dto.column;

import cz.cvut.fel.tmr.model.TaskColumn;
import cz.cvut.fel.tmr.model.Task;
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
@NoArgsConstructor
public class ColumnReadDto implements Serializable {
    private Long id;
    private String name;
    private int order;
    private Long projectId;
    private List<Long> tasks;

    public ColumnReadDto(TaskColumn taskColumn) {
        id = taskColumn.getId();
        name = taskColumn.getName();
        order = taskColumn.getColumnOrder();
        projectId = taskColumn.getProject().getId();
        if(taskColumn.getTasks().size()!=0)tasks = taskColumn.getTasks().stream().map(Task::getId).collect(Collectors.toList());
    }
}
