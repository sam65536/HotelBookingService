package com.geekhub.services.Comment;

import com.geekhub.domain.entities.Comment;
import com.geekhub.repositories.Comment.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment findOne(Long id) {
        return commentRepository.findOne(id);
    }

    @Override
    public List<Comment> getHotelComments(Long hotelId) {
        return commentRepository.getHotelComments(hotelId);
    }

    @Override
    public List<Comment> getUserComments(Long userId) {
        return commentRepository.getUserComments(userId);
    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void delete(Long id) {
        commentRepository.delete(id);
    }
}