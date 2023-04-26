package ru.yandex.practicum.filmorate.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated

public class UserController {

    private final List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> findAll() {
        log.info("Текущее количество постов: {}", users.size());
        return users;
    }

    @PostMapping
    @ResponseBody
    public User addUser(@RequestBody @Valid User user, BindingResult result) {
        log.info("Adding new user: {}", user);
        if (result.hasErrors()) {
            throw new ValidationException("Invalid user data");
        }
        user.setId(users.size() + 1);
        users.add(user);
        log.info("New user added: {}", user);
        return user;
    }

    @PutMapping
    @ResponseBody
    public User updateUser(@RequestBody @Valid User userToUpdate,
                           BindingResult result) {
        log.info("Updating user: {}", userToUpdate);

        if (result.hasErrors()) {
            throw new ValidationException("Invalid user data");
        }

        // Ищем пользователя в списке по id
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getId() == userToUpdate.getId())
                .findFirst();

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(userToUpdate.getName());
            existingUser.setEmail(userToUpdate.getEmail());
            existingUser.setBirthday(userToUpdate.getBirthday());
            existingUser.setLogin(userToUpdate.getLogin());

            // ... и т.д.
            log.info("User updated: {}", existingUser);
            return existingUser;
        } else {
            throw new ValidationException("User not found");
        }
    }
}
