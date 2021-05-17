package cz.cvut.fel.tmr.dto.trackedtime;

import cz.cvut.fel.tmr.model.TrackedTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrackedTimeReadDto {
    private Long id;
    private Long taskId;
    private Long userId;
    private LocalDateTime start;
    private LocalDateTime end;

    public TrackedTimeReadDto(TrackedTime trackedTime) {
        this.id = trackedTime.getId();
        this.taskId = trackedTime.getTask().getId();
        this.userId = trackedTime.getUser().getId();
        this.start = trackedTime.getTimeStart();
        this.end = trackedTime.getTimeEnd();
    }
}
