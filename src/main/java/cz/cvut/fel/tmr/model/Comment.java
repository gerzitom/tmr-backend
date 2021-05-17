package cz.cvut.fel.tmr.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(name = "Comment", description = "Comment assigned to task by user", oneOf = Comment.class)
@NamedQueries({
        @NamedQuery(name = "Comment.findByTask", query = "SELECT c from Comment c WHERE :task = c.task.id ORDER BY c.created DESC")
})
public class Comment extends AbstractEntity{

    private String text;

    @ManyToOne
    private User user;

    private LocalDateTime created;

    @ManyToOne
    private Task task;

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                ", user=" + user +
                '}';
    }
}
