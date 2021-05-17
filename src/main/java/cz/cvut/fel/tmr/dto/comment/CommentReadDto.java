package cz.cvut.fel.tmr.dto.comment;

import cz.cvut.fel.tmr.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentReadDto implements Serializable {
    private Long id;
    private String text;
    private LocalDateTime created;
    private Long taskId;
    private Long userId;

    public CommentReadDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.created = comment.getCreated();
        this.taskId = comment.getTask().getId();
        this.userId = comment.getUser().getId();
    }
}
