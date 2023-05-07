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
import java.util.Set;

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
            user.getFriends().add(friend.getId());
            friend.getFriends().add(user.getId());
            userStorage.updateUser(userId, user);
            userStorage.updateUser(friendId, friend);
            log.info("New friend for user {} added", userId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public void removeFriend(int userId, int friendId) {
        log.info("Removing friend {} for user {}", friendId, userId);
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            userStorage.updateUser(userId, user);
            userStorage.updateUser(friendId, friend);
            log.info("Friend {} is removed for user {}", friendId, userId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    public List<User> getCommonFriends(int user1Id, int user2Id) {
        log.info("Finding common friends");
        User user1 = userStorage.findUserById(user1Id);
        User user2 = userStorage.findUserById(user2Id);
        List<User> commonFriends = new ArrayList<>();
        if (user1 != null && user2 != null) {
            List<Integer> user1Friends = new ArrayList<>(user1.getFriends());
            List<Integer> user2Friends = new ArrayList<>(user2.getFriends());
            for (Integer userId : user1Friends) {
                if (user2Friends.contains(userId)) {
                    commonFriends.add(userStorage.findUserById(userId));
                }
            }
            log.info("Common friends are found");
            return commonFriends;
        }
        log.info("Common friends are not found");
        return commonFriends;
    }

    public List<User> getFriends(int userId) {
        log.info("Getting friend for user {}", userId);
        User user = userStorage.findUserById(userId);
        if (user != null) {
            List<User> friendsList = new ArrayList<>();
            Set<Integer> friends = user.getFriends();
            if (!friends.isEmpty()) {
                for (Integer friendId : friends) {
                    if (friendId != null) {
                        friendsList.add(userStorage.findUserById(friendId));
                    }
                }
            }
            return friendsList;
        } else {
            throw new NotFoundException("User not found");
        }
    }
}