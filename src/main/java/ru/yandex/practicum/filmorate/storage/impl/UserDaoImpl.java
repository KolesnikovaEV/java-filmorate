package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class UserDaoImpl implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
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
            throw new NotFoundException("User not found");
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
        //String sqlQueryUpdateFriends = "update USER_FRIENDS set USER_ID = ? where FRIEND_ID = ?";

        int updatedRows = jdbcTemplate.update(sqlQueryUpdateUser,
                userToUpdate.getEmail(),
                userToUpdate.getLogin(),
                userToUpdate.getName(),
                userToUpdate.getBirthday().toString(),
                id);

        if (updatedRows == 0) {
            throw new NotFoundException("User not found");
        }

        jdbcTemplate.update(sqlQueryUpdateLikes,
                userToUpdate.getId(),
                id);

//        jdbcTemplate.update(sqlQueryUpdateFriends,
//                userToUpdate.getId(),
//                id);

        userToUpdate.setId(id);
        log.info("Пользователь с идентификатором {} обновлен: {}", id, userToUpdate);
        return userToUpdate;
    }

    public boolean areFriends(int userId1, int userId2) {
        String sqlQueryGet = "SELECT CONFIRM FROM USER_FRIENDS WHERE (USER_ID = ? AND FRIEND_ID = ?)";

        try {
            boolean confirm = jdbcTemplate.queryForObject(sqlQueryGet,
                    new Object[] { userId1, userId2 },
                    Boolean.class);
            return confirm;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
    public boolean haveFriend(int userId1, int userId2) {
        String sqlQueryGetCount = "SELECT COUNT(*) FROM USER_FRIENDS WHERE (USER_ID = ? AND FRIEND_ID = ?)";
        int count = jdbcTemplate.queryForObject(sqlQueryGetCount,
                new Object[] { userId1, userId2 },
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
        List<User> friends = new ArrayList<>(); // создаем пустой список друзей
        String sqlCountQuery = "SELECT COUNT(FRIEND_ID) FROM USER_FRIENDS WHERE USER_ID = ?";
        int count = jdbcTemplate.queryForObject(sqlCountQuery, Integer.class, userId);
        if (count == 0) {
            return friends;
        }
        for(int i = 0; i<count; i++) {
            String sql = "SELECT FRIEND_ID FROM USER_FRIENDS WHERE USER_ID = ?";
            int friendId = jdbcTemplate.queryForObject(sql,
                    new Object[] { userId },
                    Integer.class);

            User friend = findUserById(friendId); // получаем данные о друге из таблицы USERS
            friends.add(friend); // добавляем друга в список друзей

        }

        return friends; // возвращаем список друзей
    }

    @Override
    public void deleteUser(int id) {
        String sqlQuery = "DELETE from USERS where id = ?";
        int deletedRows = jdbcTemplate.update(sqlQuery, id);
        if (deletedRows == 0) {
            throw new NotFoundException("User not found");
        }
        log.info("Пользователь с идентификатором {} удален.", id);
    }

}
