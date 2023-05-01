package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.control.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserControllerTest {
    private static UserService userService = new UserService();
    private static UserController userController = new UserController(userService);


    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    public void shouldNotAddUserWithWrongEmail() {
        User user = new User(1, "qw", "login", "name",
                LocalDate.of(1996, 12, 22));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> validates = validator.validate(user);
            if (!validates.isEmpty()) {
                throw new ConstraintViolationException(validates);
            }
        });
    }

    @Test
    public void shouldAddUserWithEmptyName() {
        User user = new User(1, "qw@qw.ru", "login", "",
                LocalDate.of(1996, 12, 22));

        userController.addUser(user);
        assertEquals("login", user.getName());
    }

    @Test
    public void shouldNotAddUserWithEmptyLogin() {
        User user = new User(1, "qw@qw.ru", "", "name",
                LocalDate.of(1996, 12, 22));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> validates = validator.validate(user);
            if (!validates.isEmpty()) {
                throw new ConstraintViolationException(validates);
            }
        });
    }

    @Test
    public void shouldNotAddUserWithWrongBirthday() {
        User user = new User(1, "qw", "login", "name",
                LocalDate.of(2040, 12, 22));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<User>> validates = validator.validate(user);
            if (!validates.isEmpty()) {
                throw new ConstraintViolationException(validates);
            }
        });
    }

    @Test
    public void shouldGetListOfUsers() {
        User user = new User(1, "qw@qw.ru", "login", "name",
                LocalDate.of(1996, 12, 22));
        User user2 = new User(2, "qw2@qw.ru", "login2", "name2",
                LocalDate.of(1995, 12, 22));

        userController.addUser(user);
        userController.addUser(user2);

        assertEquals(List.of(user, user2), userController.findAll());
    }

    @Test
    public void shouldUpdateUser() {
        User user = new User(1, "qw@qw.ru", "login", "name",
                LocalDate.of(1996, 12, 22));
        User user2 = new User(1, "qw2@qw.ru", "updatelogin", "name2",
                LocalDate.of(1995, 12, 22));

        userController.addUser(user);
        userController.updateUser(user2);

        assertEquals(List.of(user), userController.findAll());
    }

    @Test
    public void shouldNotUpdateUser() throws IOException, InterruptedException {
        User user = new User(1, "qw@qw.ru", "login", "name",
                LocalDate.of(1996, 12, 22));
        User userToUpdate = new User(9999, "qw2@qw.ru", "updatelogin", "name2",
                LocalDate.of(1995, 12, 22));

        userController.addUser(user);

        Exception exception = assertThrows(NotFoundException.class, () -> userController.updateUser(userToUpdate));
        assertEquals("User not found", exception.getMessage());

    }
}
