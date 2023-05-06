package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User addUser(User user);

    User updateUser(int id, User userToUpdate);

    User findUserById(int id);
}
