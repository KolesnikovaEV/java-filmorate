package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDaoImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDaoImpl userStorage;

    @Test
    public void testAddUser() {
        User expected = User.builder()
                .email("test_email")
                .login("test_login")
                .name("test_name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User actual = userStorage.addUser(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindUserById() {
        User expected = User.builder()
                .id(1)
                .email("test_email")
                .login("test_login")
                .name("test_name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User actual = userStorage.findUserById(1);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAllUsers() {
        List<User> users = userStorage.findAllUsers();

        assertNotNull(users);
        assertTrue(users.size() > 0);
    }
}
