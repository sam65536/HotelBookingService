package com.geekhub.repositories.Comment;

import com.geekhub.domain.entities.Comment;

import java.util.List;

public interface CommentRepository {
    public List<Comment> findAll();
    public Comment findOne(Long id);
    public List<Comment> getComments(Long hotelId);
    public void save (Comment comment);
    public void delete (Long id);
}
