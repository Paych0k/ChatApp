package edu.school21.repositories;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public class MessageRepositoryImpl implements MessageRepository {


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MessageRepositoryImpl(HikariDataSource hikariDataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(hikariDataSource);
    }

    @Override
    public List<Message> findByUsernameId(Long usernameId) {
        String sql =
                "SELECT message.id, message.username_id, message.text, message.message_time, message.chatroom_id from message join " +
                        "public.users u on u.id = message.username_id where u.username = :username";
        MapSqlParameterSource params = new MapSqlParameterSource("username", usernameId);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Message.class));
    }

    @Override
    public void save(Message message) {
        String sql = "INSERT INTO message (username_id, text, message_time, chatroom_id) VALUES (:usernameId, :text, :messageTime, :chatRoomId)";

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("usernameId", message.getUsernameId())
                .addValue("text", message.getText())
                .addValue("messageTime", message.getMessageTime())
                .addValue("chatRoomId", message.getChatRoomId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder, new String[]{"id"});

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            message.setId(keyHolder.getKey().longValue());
        }
    }


    @Override
    public List<Message> findAll() {
        String sql = "SELECT * from message";
        return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Message.class));
    }

    @Override
    public Optional<Message> findById(long id) {
        String sql = "SELECT * from message WHERE id = :id";
        Optional<Message> message = Optional.empty();
        try {
            message = namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("id", id),
                    new BeanPropertyRowMapper<>());
        } catch (DataAccessException ignore) {
        }
        return message;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM message WHERE id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

    public List<Message> findRecentMessagesByChatRoomId(Long chatRoomId, int limit) {
        String sql = "SELECT * FROM message WHERE chatroom_id = :chatRoomId ORDER BY message_time DESC LIMIT :limit";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("chatRoomId", chatRoomId)
                .addValue("limit", limit);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Message.class));
    }

}
