package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Map<Integer, User> users = new HashMap<>();

    public List<User> findAllUsers() {
        log.info("Finding all users");
        log.info("Found {} users", users.size());
        return new ArrayList<>(users.values());
    }

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

    public User updateUser(User userToUpdate) {
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
}