package cz.cvut.fel.tmr.dto.trackedtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tmr.dto.Dto;
import cz.cvut.fel.tmr.model.TrackedTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackedTimeDto implements Serializable, Dto<TrackedTime> {
    private Long taskId;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Override
    public TrackedTime buildFromDto() {
        return null;
    }

    @Override
    public TrackedTime update(TrackedTime trackedTime) {
        // TODO resolve taskId and userId
        if(this.startTime != null) trackedTime.setTimeStart(this.startTime);
        if(this.endTime != null) trackedTime.setTimeEnd(this.endTime);
        return trackedTime;
    }

    @Override
    public String toString() {
        return "TrackedTimeDto{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                ", start=" + startTime +
                ", end=" + endTime +
                '}';
    }
}
