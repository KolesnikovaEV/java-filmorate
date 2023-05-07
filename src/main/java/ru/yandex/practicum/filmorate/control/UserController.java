package ru.yandex.practicum.filmorate.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> findAll() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return userStorage.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User userToUpdate) {
        return userStorage.updateUser(userToUpdate.getId(), userToUpdate);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        List<User> friends = userService.getFriends(userId);
        return friends;
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherId) {
        List<User> commonFriends = userService.getCommonFriends(userId, otherId);
        return commonFriends;
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        User user = userStorage.findUserById(userId);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userStorage.deleteUser(userId);
    }
}