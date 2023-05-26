package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Component
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public User findUserById(int id) {
        String sqlQuery = "select * from USERS where id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EmptyResultDataAccessException("User not found", 1);
        }
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "select * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into USERS (email, login, name, birthday) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));

            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        log.info("Добавлен новый пользователь: {}", user);

        return user;
    }

    @Override
    public User updateUser(int id, User userToUpdate) {
        String sqlQueryUpdateUser = "update USERS set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        String sqlQueryUpdateLikes = "update FILM_LIKES set USER_ID = ? where USER_ID = ?";

        int updatedRows = jdbcTemplate.update(sqlQueryUpdateUser,
                userToUpdate.getEmail(),
                userToUpdate.getLogin(),
                userToUpdate.getName(),
                userToUpdate.getBirthday().toString(),
                id);

        if (updatedRows == 0) {
            throw new NotFoundException("User not found with id: " + id);
        }

        jdbcTemplate.update(sqlQueryUpdateLikes,
                userToUpdate.getId(),
                id);

        userToUpdate.setId(id);
        log.info("Пользователь с идентификатором {} обновлен: {}", id, userToUpdate);
        return userToUpdate;
    }

    public boolean areFriends(int userId1, int userId2) {
        String sqlQueryGet = "SELECT CONFIRM FROM USER_FRIENDS WHERE (USER_ID = ? AND FRIEND_ID = ?)";

        try {
            boolean confirm = jdbcTemplate.queryForObject(sqlQueryGet,
                    new Object[]{userId1, userId2},
                    Boolean.class);
            return confirm;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public boolean haveFriend(int userId1, int userId2) {
        String sqlQueryGetCount = "SELECT COUNT(*) FROM USER_FRIENDS WHERE (USER_ID = ? AND FRIEND_ID = ?)";
        int count = jdbcTemplate.queryForObject(sqlQueryGetCount,
                new Object[]{userId1, userId2},
                Integer.class);

        return count > 0;
    }

    @Override
    public void addFriend(int userId1, int userId2) {
        User user1 = findUserById(userId1);
        log.info("Adding friend for user {}", userId1);

        if (haveFriend(userId1, userId2) || areFriends(userId1, userId2)) {
            log.info("User {} is already a friend of user {}", userId2, userId1);
            return;
        }

        String sqlQueryAddFriend = "INSERT INTO USER_FRIENDS (USER_ID, FRIEND_ID) VALUES ( ?, ? )";
        jdbcTemplate.update(sqlQueryAddFriend,
                userId1,
                userId2);
        if (haveFriend(userId2, userId1)) {
            String sqlQueryAddConfirm = "INSERT INTO USER_FRIENDS (CONFIRM) VALUES ( ? )";
            jdbcTemplate.update(sqlQueryAddConfirm,
                    true);
        }
        List<Integer> friends = user1.getFriends();
        friends.add(userId2);
        user1.setFriends(friends);

        log.info("New friend for user {} added", userId1);
    }

    @Override
    public List<User> getFriends(int userId) {
        String filmQuery = "SELECT COUNT(ID) FROM USERS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(filmQuery, Integer.class, userId);
        if (count == 0) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        String sql = "SELECT U.* FROM USERS U INNER JOIN USER_FRIENDS UF ON U.ID = UF.FRIEND_ID WHERE UF.USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int user1Id, int user2Id) {
        String sqlQuery = "SELECT U.* FROM USERS U " +
                "WHERE U.ID IN (" +
                "SELECT UF.FRIEND_ID FROM USER_FRIENDS UF " +
                "WHERE UF.USER_ID = ? AND UF.FRIEND_ID IN " +
                "(SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?))";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, user1Id, user2Id);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM USER_FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";
        int rowsDeleted = jdbcTemplate.update(sql, userId, friendId);
        if (rowsDeleted == 0) {
            log.info("Friendship not found for user {} and friend {}", userId, friendId);
        } else {
            log.info("Friendship deleted for user {} and friend {}", userId, friendId);
        }
    }

    @Override
    public void deleteUser(int id) {
        String sqlQuery = "DELETE from USERS where id = ?";
        int deletedRows = jdbcTemplate.update(sqlQuery, id);
        if (deletedRows == 0) {
            throw new NotFoundException("User not found" + id);
        }
        log.info("Пользователь с идентификатором {} удален.", id);
    }
}
