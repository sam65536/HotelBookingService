package com.geekhub.services.Comment;

import com.geekhub.domain.entities.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAll();
    Comment findOne(Long id);
    List<Comment> getHotelComments(Long hotelId);
    List<Comment> getUserComments(Long userId);
    void save(Comment comment);
    void delete(Long id);
}
