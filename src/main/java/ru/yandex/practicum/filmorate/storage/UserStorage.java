package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User addUser(User user);

    User updateUser(int id, User userToUpdate);

    User findUserById(int id);

    void deleteUser(int id);

    void addFriend(int userId1, int userId2);

    List<User> getFriends(int userId);

    void removeFriend(int userId, int friendId);

    List<User> getCommonFriends(int user1Id, int user2Id);
}
