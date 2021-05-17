package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.TaskDao;
import cz.cvut.fel.tmr.dao.TrackedTimeDao;
import cz.cvut.fel.tmr.dao.UserDao;
import cz.cvut.fel.tmr.dto.task.TaskTrackedTimeByUserDto;
import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeDto;
import cz.cvut.fel.tmr.dto.trackedtime.TrackedTimeReadDto;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.TrackedTime;
import cz.cvut.fel.tmr.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class TrackedTimeService {
    private final TrackedTimeDao dao;
    private final UserDao userDao;
    private final TaskDao taskDao;

    @Autowired
    public TrackedTimeService(TrackedTimeDao dao, UserDao userDao, TaskDao taskDao) {
        this.dao = dao;
        this.userDao = userDao;
        this.taskDao = taskDao;
    }

    @Transactional
    public void endTrack(TrackedTime time){
        time.setTimeEnd(now());
        dao.update(time);
    }

    @Transactional
    public void changeDescription(TrackedTime time, String description){
        time.setDescription(description);
        dao.update(time);
    }

    @Transactional
    public Long addTrackedTime(TrackedTimeDto dto){
        TrackedTime trackedTime = buildFromDto(dto);

        // check if user is tracking time
        TrackedTime userTrackedTimeNow = dao.findByUserActiveUse(trackedTime.getUser().getId());
        if(userTrackedTimeNow != null) throw new AlreadyExistsException("User is currently tracking time");

        dao.persist(trackedTime);
        return trackedTime.getId();
    }

    /**
     * @deprecated
     * @return all tracked times
     */
    @Transactional

    public List<TrackedTimeReadDto> findAll(){
        List<TrackedTime> times = dao.findAll();
        return dao.findAll().stream().map(TrackedTimeReadDto::new).collect(Collectors.toList());
    }

    @Transactional
    public TrackedTimeReadDto findByUserActiveUse(Long userId){
        TrackedTime trackedTime = dao.findByUserActiveUse(userId);
        if(trackedTime == null) return null;
        else return new TrackedTimeReadDto(trackedTime);
    }

    @Transactional
    public void update(Long id, TrackedTimeDto dto) {
        TrackedTime trackedTime = dao.find(id);
        trackedTime = dto.update(trackedTime);
        dao.update(trackedTime);
    }

    @Transactional
    public void remove(Long id){
        TrackedTime trackedTime = dao.find(id);
        dao.remove(trackedTime);
    }

    @Transactional
    public Map<Long, Long> getTaskTrackedTimeBrief(Long taskId){
        List<TrackedTime> trackedTimes = dao.findByTask(taskId);
        Map<Long, Long> userTimesMap = new HashMap<>();
        List<TaskTrackedTimeByUserDto> ret = new ArrayList<>();
        for(TrackedTime trackedTime : trackedTimes){
            Long userId = trackedTime.getUser().getId();
            if(userTimesMap.containsKey(userId)){
                Long elapsedTime = userTimesMap.get(userId);
                elapsedTime += trackedTime.getElapsedTime();
                userTimesMap.put(userId, elapsedTime);
            } else {
                userTimesMap.put(userId, trackedTime.getElapsedTime());
            }
        }
        return userTimesMap;
    }

    private TrackedTime buildFromDto(TrackedTimeDto dto){
        TrackedTime trackedTime = new TrackedTime();
        User user = userDao.find(dto.getUserId());
        if(user == null) throw new NotFoundException("User not found");
        trackedTime.setUser(user);
        Task task = taskDao.find(dto.getTaskId());
        if(task == null) throw new NotFoundException("Task not found");
        trackedTime.setTask(task);
        trackedTime.setTimeStart(dto.getStartTime());
        return trackedTime;
    }

}
