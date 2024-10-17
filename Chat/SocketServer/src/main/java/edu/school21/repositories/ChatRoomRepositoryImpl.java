package edu.school21.repositories;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.models.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChatRoomRepositoryImpl implements ChatRoomRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ChatRoomRepositoryImpl(HikariDataSource hikariDataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(hikariDataSource);
        String sql = "DELETE from chatroom_participant";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    @Override
    public void create(String name, Long ownerId) {
        String sql = "INSERT INTO chatroom (name, owner_id) VALUES (:name, :ownerId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        params.addValue("ownerId", ownerId);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<ChatRoom> findById(Long id) {
        String sql = "SELECT * FROM chatroom WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);

        List<ChatRoom> chatRooms = namedParameterJdbcTemplate.query(sql, params,
                new BeanPropertyRowMapper<>(ChatRoom.class));
        return chatRooms.stream().findFirst();
    }

    @Override
    public List<ChatRoom> findAll() {
        String sql = "SELECT * FROM chatroom";
        return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ChatRoom.class));
    }

    @Override
    public void addParticipant(Long chatRoomId, Long userId) {
        String sql = "INSERT INTO chatroom_participant (chatroom_id, user_id) VALUES (:chatRoomId, :userId)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", chatRoomId);
        params.addValue("userId", userId);

        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void removeParticipant(Long chatRoomId, Long userId) {
        String sql = "DELETE FROM chatroom_participant WHERE chatroom_id = :chatRoomId AND user_id = :userId";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chatRoomId", chatRoomId);
        params.addValue("userId", userId);

        namedParameterJdbcTemplate.update(sql, params);
    }
}