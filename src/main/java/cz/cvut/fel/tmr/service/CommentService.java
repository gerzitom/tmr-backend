package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.CommentDao;
import cz.cvut.fel.tmr.dao.TaskDao;
import cz.cvut.fel.tmr.dao.UserDao;
import cz.cvut.fel.tmr.dto.comment.CommentDto;
import cz.cvut.fel.tmr.exception.NotFoundException;
import cz.cvut.fel.tmr.model.Comment;
import cz.cvut.fel.tmr.model.Task;
import cz.cvut.fel.tmr.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
public class CommentService {

    private final CommentDao dao;
    private final TaskDao taskDao;
    private final UserService userService;

    private final UserDao userDao;

    @Autowired
    public CommentService(CommentDao dao, TaskDao taskDao, UserService userService, UserDao userDao) {
        this.dao = dao;
        this.taskDao = taskDao;
        this.userService = userService;
        this.userDao = userDao;
    }
    /**
     * @param taskId
     * @return Ordered comments
     */
    @Transactional
    public List<Comment> findAll(Long taskId){
        Objects.requireNonNull(taskId);
        Task task = taskDao.find(taskId);
        User currentUser = userService.getSecurityUser();
        if(!task.getProject().isUserInProject(currentUser.getId()) ||
                !task.getProject().getOrganization().isUserPM(currentUser.getId()))
            throw new NotFoundException("User has no rights to see selected task");
        return dao.findByTask(taskId);
    }

    public Comment buildFromDto(CommentDto dto) throws NotFoundException{
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreated(dto.getCreated());
        User user = userDao.find(dto.getUserId());
        if(user == null) throw new NotFoundException("User not found");
        comment.setUser(userDao.find(dto.getUserId()));
        return comment;
    }
}
