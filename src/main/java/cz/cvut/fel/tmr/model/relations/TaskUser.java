package cz.cvut.fel.tmr.model.relations;

import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
        @NamedQuery(name = "TaskUser.find", query = "SELECT u from TaskUser u WHERE :taskId = u.task.id AND :userId = u.user.id"),
        @NamedQuery(name = "Task.findByUser", query = "SELECT t FROM TaskUser t WHERE :userId = t.user.id"),
        @NamedQuery(name = "TaskUser.findUpcommingTasks", query = "SELECT t FROM TaskUser t WHERE :userId = t.user.id AND t.task.state <> 'DONE' AND t.task.deadline <> NULL ORDER BY t.task.deadline")
})
public class TaskUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    Task task;

    private LocalDateTime created;
}
