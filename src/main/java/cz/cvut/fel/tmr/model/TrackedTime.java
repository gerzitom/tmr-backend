package cz.cvut.fel.tmr.model;

import cz.cvut.fel.tmr.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Tracked time", description = "Time tracked to task", oneOf = TrackedTime.class)
@NamedQueries({
        @NamedQuery(name = "TrackedTime.findActiveTrackedTimeByUser", query = "SELECT t from TrackedTime t WHERE :user = t.user.id AND t.timeEnd is NULL"),
        @NamedQuery(name = "TrackedTime.getTaskTrackedTimes", query = "SELECT t from TrackedTime t WHERE :task = t.task.id AND t.timeStart IS NOT NULL AND t.timeEnd IS NOT NULL")
})
public class TrackedTime{

    @Id
    @GeneratedValue
    private Long id;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;
    private String description;

    @ManyToOne
    private User user;

    @ManyToOne
    private Task task;

    /**
     * Gets elapsed seconds of tracked time
     * @return elapsed seconds of tracked time
     */
    public Long getElapsedTime(){
        if(this.timeStart == null || this.timeEnd == null) throw new ValidationException("Times are not set.");
        return this.timeStart.until(this.timeEnd, ChronoUnit.SECONDS);
    }
}
