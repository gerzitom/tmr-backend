package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.*;
import cz.cvut.fel.tmr.dto.column.ColumnDto;
import cz.cvut.fel.tmr.dto.column.ColumnReadDto;
import cz.cvut.fel.tmr.exception.AlreadyExistsException;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.exception.ValidationException;
import cz.cvut.fel.tmr.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class ColumnService {
    private final ColumnDao columnDao;
    private final ProjectDao projectDao;
    private final TaskDao taskDao;
    private final UserService userService;

    @Autowired
    public ColumnService(ColumnDao columnDao, ProjectDao projectDao, TaskDao taskDao, UserService userService) {
        this.columnDao = columnDao;
        this.projectDao = projectDao;
        this.taskDao = taskDao;
        this.userService = userService;
    }

    private TaskColumn buildFromDto(ColumnDto dto){
        TaskColumn taskColumn = new TaskColumn();
        taskColumn.setName(dto.getName());
        taskColumn.setColumnOrder(dto.getOrder());
        taskColumn.setProject(projectDao.find(dto.getProjectId()));
        return taskColumn;
    }

    @Transactional
    public Long persist(ColumnDto columnDto){
        if(columnDto.getName().isEmpty()) throw new ValidationException("Name of the column can not be empty");

        Project foundProject = projectDao.find(columnDto.getProjectId());
        if(foundProject == null) throw new NotFoundException("Project for column not found");

        User currentUser = userService.getSecurityUser();
        if(!foundProject.isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        if(columnDto.getOrder()<0 || columnDto.getOrder()>foundProject.getTaskColumns().size()) throw new ValidationException("Given order is invalid");

        TaskColumn foundTaskColumn = columnDao.findByNameAndProject(columnDto.getName(), columnDto.getProjectId());
        if(foundTaskColumn != null) throw new AlreadyExistsException("TaskColumn with this name already exists");

        TaskColumn newTaskColumn = buildFromDto(columnDto);

        columnDao.persist(newTaskColumn);
        foundProject.getTaskColumns().forEach(column -> {
            if (column.getColumnOrder()>= newTaskColumn.getColumnOrder()){
                column.setColumnOrder(column.getColumnOrder()+1);
                columnDao.update(column);}
            });

        return newTaskColumn.getId();
    }

    @Transactional
    public List<TaskColumn> findByProject(Long projectId){
        Objects.requireNonNull(projectId);
        return columnDao.findByProject(projectId);
    }

    @Transactional
    public void update(Long columnId, ColumnDto columnDto){
        Objects.requireNonNull(columnDto);
        TaskColumn originalTaskColumn = columnDao.find(columnId);
        Project project = originalTaskColumn.getProject();

        User currentUser = userService.getSecurityUser();
        if(!project.isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");

        if (columnDto.getOrder()> originalTaskColumn.getColumnOrder()){
            project.getTaskColumns().forEach(column -> {
                if (column.getColumnOrder()<=columnDto.getOrder() && column.getColumnOrder()> originalTaskColumn.getColumnOrder()){
                    column.setColumnOrder(column.getColumnOrder()-1);
                    columnDao.update(column); }
            });
        }
        if (columnDto.getOrder()< originalTaskColumn.getColumnOrder()){
            project.getTaskColumns().forEach(column -> {
                if (column.getColumnOrder()>=columnDto.getOrder() && column.getColumnOrder()< originalTaskColumn.getColumnOrder()){
                    column.setColumnOrder(column.getColumnOrder()+1);
                    columnDao.update(column); }
            });
        }
        TaskColumn taskColumn = columnDto.updateColumn(originalTaskColumn);
        columnDao.update(taskColumn);
    }


    @Transactional
    public ColumnReadDto find(Long columnId){
        TaskColumn taskColumn = columnDao.find(columnId);
        if(taskColumn == null) throw new NotFoundException("TaskColumn was not found");
        userService.doesUserHaveEnoughRights(taskColumn.getProject());
        return new ColumnReadDto(taskColumn);
    }

    //When column will be removed all tasks within this column will be also removed
    @Transactional
    public void removeColumn(Long columnId){
        TaskColumn taskColumn = columnDao.find(columnId);
        User currentUser = userService.getSecurityUser();
        if(!taskColumn.getProject().isUserInProject(currentUser.getId()))
            throw new NotFoundException("User is not a project member");
        taskColumn.getProject().getTaskColumns().remove(taskColumn);
        projectDao.update(taskColumn.getProject());
        taskColumn.getProject().getTaskColumns().forEach(column -> {
            if (column.getColumnOrder()> taskColumn.getColumnOrder()){
                column.setColumnOrder(column.getColumnOrder()-1);
                columnDao.update(column);}
        });
        columnDao.remove(taskColumn);
    }

//    @Transactional
//    public void addTask(Long taskId, Long columnId) throws EarException {
//        Objects.requireNonNull(taskId);
//        Objects.requireNonNull(columnId);
//
//        Task foundTask = taskDao.find(taskId);
//        if(foundTask==null) throw new NotFoundException("Task is not found");
//
//        TaskColumn foundColumn = columnDao.find(columnId);
//        if(foundColumn==null) throw new NotFoundException("TaskColumn is not found");
//        foundTask.getTaskColumn().getTasks().remove(foundTask);
//        foundTask.setTaskColumn(foundColumn);
//        foundColumn.getTasks().add(foundTask);
//        taskDao.update(foundTask);
//    }
}
