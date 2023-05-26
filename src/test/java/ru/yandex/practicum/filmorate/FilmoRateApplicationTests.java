package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserDaoImpl;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDaoImpl userStorage;
    private final FilmDaoImpl filmStorage;

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


    @Test
    public void testUpdateUser() {

        User expected = User.builder()
                .email("test_email")
                .login("test_login")
                .name("test_name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User addedUser = userStorage.addUser(expected);
        int userId = addedUser.getId();

        User updatedUser = User.builder()
                .id(userId)
                .email("new_email")
                .login("new_login")
                .name("new_name")
                .birthday(LocalDate.of(1999, 12, 31))
                .build();

        User actual = userStorage.updateUser(userId, updatedUser);

        assertNotNull(actual.getId());
        assertEquals(updatedUser.getEmail(), actual.getEmail());
        assertEquals(updatedUser.getLogin(), actual.getLogin());
        assertEquals(updatedUser.getName(), actual.getName());
        assertEquals(updatedUser.getBirthday(), actual.getBirthday());
    }

    @Test
    public void testAddFriend() {
        User user1 = User.builder()
                .email("test_email@com")
                .login("test1_login")
                .name("test_name1")
                .birthday(LocalDate.of(2000, 2, 1))
                .build();

        User user2 = User.builder()
                .email("test2_email@com")
                .login("test2_login")
                .name("test_name2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User addedUser1 = userStorage.addUser(user1);
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());
        userStorage.addFriend(addedUser2.getId(), addedUser1.getId());


        User updatedUser2 = userStorage.findUserById(addedUser2.getId());
        assertTrue(userStorage.getFriends(addedUser1.getId()).contains(updatedUser2));
    }

    @Test
    public void testGetFriends() {
        User user1 = User.builder()
                .email("test_email_1")
                .login("test_login_1")
                .name("test_name_1")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        user1 = userStorage.addUser(user1);

        User user2 = User.builder()
                .email("test_email_2")
                .login("test_login_2")
                .name("test_name_2")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        user2 = userStorage.addUser(user2);

        List<User> friends1 = userStorage.getFriends(user1.getId());
        List<User> friends2 = userStorage.getFriends(user2.getId());

        assertTrue(friends1.isEmpty());
        assertTrue(friends2.isEmpty());

        userStorage.addFriend(user1.getId(), user2.getId());
        friends1 = userStorage.getFriends(user1.getId());
        friends2 = userStorage.getFriends(user2.getId());

        assertEquals(1, friends1.size());
        assertEquals(0, friends2.size());

    }

    @Test
    public void testAddFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(List.of(Genre.ACTION, Genre.COMEDY));
        Film expected = Film.builder()
                .name("test_name")
                .description("test_description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.PG_13)
                .genres(genres)
                .build();

        Film actual = filmStorage.addFilm(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testFindAllFilms() {
        List<Film> films = filmStorage.findAllFilms();

        assertNotNull(films);
        assertTrue(films.size() > 0);
    }

    @Test
    public void testUpdateFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(List.of(Genre.COMEDY));
        Film expected = Film.builder()
                .id(1)
                .name("updated_name")
                .description("updated_description")
                .releaseDate(LocalDate.of(2021, 1, 1))
                .duration(120)
                .mpa(Mpa.PG_13)
                .genres(genres)
                .build();

        Film actual = filmStorage.updateFilm(1, expected);

        assertEquals(expected, actual);
    }

    @Test
    public void testAddFilmWithGenres() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(List.of(Genre.ACTION, Genre.COMEDY));

        Film expected = Film.builder()
                .name("test_name")
                .description("test_description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.PG_13)
                .genres(genres)
                .build();

        Film actual = filmStorage.addFilm(expected);
        expected.setId(actual.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteFilm() {
        LinkedHashSet<Genre> genres = new LinkedHashSet<>(List.of(Genre.ACTION, Genre.COMEDY));

        Film film = Film.builder()
                .name("test_name")
                .description("test_description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(Mpa.PG_13)
                .genres(genres)
                .build();

        Film addedFilm = filmStorage.addFilm(film);

        filmStorage.deleteFilm(addedFilm.getId());

        assertThrows(NotFoundException.class, () -> filmStorage.findFilmById(addedFilm.getId()));
    }
}


