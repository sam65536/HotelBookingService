package com.geekhub.repositories.Comment;

import com.geekhub.domain.entities.Comment;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentRowMapper implements RowMapper<Comment> {
    @Override
    public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setDate(rs.getTimestamp("date").toLocalDateTime());
        comment.setStatus(rs.getBoolean("status"));
        comment.setText(rs.getString("text"));
        comment.setIsAnswer(rs.getBoolean("is_answer"));
        return comment;
    }
}