package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentDao extends BaseDao<Comment> {
    public CommentDao() {
        super(Comment.class);
    }

    public List<Comment> findByTask(Long taskId){
        return em.createNamedQuery("Comment.findByTask", Comment.class)
                .setParameter("task", taskId)
                .getResultList();
    }

}
