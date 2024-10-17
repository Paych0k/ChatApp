package edu.school21.repositories;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@Repository
public class UsersRepositoryImpl implements UsersRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UsersRepositoryImpl(HikariDataSource dataSource) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    @Override
    public Optional<User> findByName(String name) {
        String sql = "select * from users where username = :name";
        Optional<User> user = Optional.empty();
        try {
            user = Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql,
                    new MapSqlParameterSource("name", name), new BeanPropertyRowMapper<>(User.class)));
        } catch (DataAccessException ignore) {
        }
        return user;
    }

    public long getMaxId() {
        String sql = "select max(id) from users";
        Long maxId;
        maxId = namedParameterJdbcTemplate.queryForObject(sql, new HashMap<>(), Long.class);
        maxId = maxId == null ? 0 : maxId;
        return maxId;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (username, password) VALUES (:username, :password) ON CONFLICT DO NOTHING";

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("username", user.getUsername())
                .addValue("password", user.getPassword());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rowsAffected = namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder, new String[]{"id"});

        if (rowsAffected > 0 && keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().longValue());
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";
        return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }


    @Override
    public Optional<User> findById(long id) {
        String sql = "select * from users where id = :id";
        Optional<User> user = Optional.empty();
        try {
            user = Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql,
                    new MapSqlParameterSource("id", id), new BeanPropertyRowMapper<>(User.class)));
        } catch (DataAccessException ignore) {
        }
        return user;
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from users where id = :id";
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }
}
