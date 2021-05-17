package cz.cvut.fel.tmr.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "task_column")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(name = "TaskColumn", description = "TaskColumn", oneOf = TaskColumn.class)
@NamedQueries({
        @NamedQuery(name = "Column.findByProject", query = "SELECT c from TaskColumn c WHERE :project = c.project.id"),
        @NamedQuery(name = "Column.findByNameAndProject", query = "SELECT c from TaskColumn c WHERE :projectId = c.project.id AND :column = c.name"),
})
public class TaskColumn extends AbstractEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int columnOrder;

    @OneToMany(mappedBy = "taskColumn", fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

}
