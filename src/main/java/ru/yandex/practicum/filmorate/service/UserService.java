package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public UserService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        log.info("Adding friend for user {}", userId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if (user != null && friend != null) {
            userStorage.addFriend(userId, friendId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Removing friend {} for user {}", friendId, userId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if (user != null && friend != null) {
            userStorage.removeFriend(userId, friendId);
            log.info("Friend {} is removed for user {}", friendId, userId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<User> getCommonFriends(int user1Id, int user2Id) {
        log.info("Finding common friends");
        User user1 = userStorage.findUserById(user1Id);
        User user2 = userStorage.findUserById(user2Id);
        if (user1 != null && user2 != null) {
            List<User> commonFriends = userStorage.getCommonFriends(user1Id, user2Id);

            log.info("Common friends are found");
            return commonFriends;
        }
        log.info("Common friends are not found");
        return new ArrayList<>();
    }

    public List<User> getFriends(int userId) {
        log.info("Getting friend for user {}", userId);
        User user = userStorage.findUserById(userId);
        if (user != null) {
            try {
                return userStorage.getFriends(userId);
            } catch (NotFoundException e) {
                throw new NotFoundException("Friends not found");
            }

        } else {
            throw new NotFoundException("User not found");
        }
    }
}