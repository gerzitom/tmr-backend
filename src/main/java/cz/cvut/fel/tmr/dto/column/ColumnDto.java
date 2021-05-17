package cz.cvut.fel.tmr.dto.column;


import cz.cvut.fel.tmr.model.TaskColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDto implements Serializable {
    private String name;
    private int order;
    private Long projectId;

    public ColumnDto(TaskColumn taskColumn){
        this.name = taskColumn.getName();
        this.order = taskColumn.getColumnOrder();
        this.projectId = taskColumn.getProject().getId();
    }

    public TaskColumn updateColumn(TaskColumn taskColumn){
        if(this.name != null) taskColumn.setName(this.name);
        taskColumn.setColumnOrder(this.order);
        return taskColumn;
    }
}
