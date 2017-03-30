package com.geekhub.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.geekhub.domain.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

	Comment findByText(String text);
	
	@Query(value="select * from comment where hotel_id=?1 order by date desc",nativeQuery=true)
	Iterable<Comment> getComments(long hotel_id);
}