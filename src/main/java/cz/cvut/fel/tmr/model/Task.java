package cz.cvut.fel.tmr.model;

import cz.cvut.fel.tmr.model.relations.TaskUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Task", description = "Task", oneOf = Task.class)
@NamedQueries({
        @NamedQuery(name = "Task.findByProject", query = "SELECT t from Task t WHERE :project = t.project.id"),
        @NamedQuery(name = "Task.findByColumn", query = "SELECT t from Task t WHERE :column = t.taskColumn.id"),
        @NamedQuery(name = "Task.findByProjectAndState", query = "SELECT t from Task t WHERE :project = t.project.id AND t.state = :state"),
})
public class Task extends AbstractEntity{

    private String name;

    private String description;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    private State state = State.IN_PROGRESS;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaskUser> taskUsers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne
    private User responsibleUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentTask")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Task> subtasks = new ArrayList<>();

    @ManyToOne
    private Task parentTask;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "task")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy("created DESC")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE)
    private List<TrackedTime> trackedTimes;

    @ManyToOne
    @JoinColumn(name = "column_id")
    private TaskColumn taskColumn;


    public Task(String name, String description, LocalDate deadline, User responsibleUser) {
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.responsibleUser = responsibleUser;
    }

    public void addComment(Comment comment){ this.comments.add(comment); }

    public void addSubtask(Task subtask){ this.subtasks.add(subtask); }

    public Long getParentTaskId(){
        return parentTask == null ? null : parentTask.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                '}';
    }
}
