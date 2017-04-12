package com.geekhub.repositories.Comment;

import com.geekhub.domain.entities.Comment;
import com.geekhub.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT id, date, is_answer, status, text FROM comment";
        List<Comment> comments = this.jdbcTemplate.query(sql, new CommentRowMapper());
        return comments;
    }

    @Override
    public Comment findOne(Long id) {
        String sql = "SELECT id, date, is_answer, status, text FROM comment WHERE id=" + id;
        List<Comment> comments = this.jdbcTemplate.query(sql, new CommentRowMapper());
        return comments.get(0);
    }

    @Override
    public List<Comment> getCommentsOfHotel(Long hotelId) {
        String sql = "SELECT comment.id, date, is_answer, status, text, user_id, "
                + "\"user\".name FROM comment LEFT JOIN \"user\" ON user_id=\"user\".id "
                + "WHERE hotel_id=" + hotelId + "ORDER BY date DESC";
        List<Comment> comments = this.jdbcTemplate.query(sql,
                (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setDate(rs.getTimestamp("date").toLocalDateTime());
            comment.setText(rs.getString("text"));
            comment.setIsAnswer(rs.getBoolean("is_answer"));
            comment.setStatus(rs.getBoolean("status"));
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setName(rs.getString("name"));
            comment.setUser(user);
            return comment;
        });
        return comments;
    }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comment (id, date, status, text, is_answer) VALUES (?, ?, ?, ?, ?)";
        this.jdbcTemplate.update(sql, new Object[] {comment.getId(), comment.getDate(),
                comment.getStatus(), comment.getText(), comment.getIsAnswer()});
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comment WHERE id=" + id;
        this.jdbcTemplate.update(sql);
    }
}