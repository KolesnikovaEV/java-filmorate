package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        log.info("Finding all users");
        log.info("Found {} users", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        try {
            log.info("Adding new user: {}", user);
            user.setId(users.size() + 1);
            users.put(user.getId(), user);
            log.info("New user added: {}", user);
            return user;
        } catch (ValidationException e) {
            throw new ValidationException("Invalid user data");
        }
    }

    @Override
    public User updateUser(int id, User userToUpdate) {
        log.info("Updating user: {}", userToUpdate);

        User existingUser = users.get(userToUpdate.getId());
        if (existingUser != null) {
            existingUser.setName(userToUpdate.getName());
            existingUser.setEmail(userToUpdate.getEmail());
            existingUser.setBirthday(userToUpdate.getBirthday());
            existingUser.setLogin(userToUpdate.getLogin());

            users.put(existingUser.getId(), existingUser);

            log.info("User updated: {}", existingUser);
            return existingUser;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public User findUserById(int id) {
        log.info("Finding user {}", id);
        User existingUser = users.get(id);
        if (existingUser != null) {
            log.info("User {} is founded", id);
            return existingUser;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public void deleteUser(int id) {
        log.info("Removing user {}", id);
        User existingUser = users.get(id);
        if (existingUser != null) {
            log.info("User deleted {}", id);
            users.remove(id);
        } else {
            throw new NotFoundException("User not found");
        }
    }
}
