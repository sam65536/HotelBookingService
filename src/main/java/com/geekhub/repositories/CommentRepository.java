package com.geekhub.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    @Query(value = "select * from comment where hotel_id=?1 order by date desc", nativeQuery = true)
    Iterable<Comment> getComments(long hotelId);
}