package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(int userId, int filmId) {
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);
        if (user != null && film != null) {
            //film.getLikes().add(userId);
            filmStorage.addLikes(userId, filmId);
        } else {
            throw new NotFoundException("Film or User not found");
        }
    }

    public void removeLike(int userId, int filmId) {
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);
        if (user != null && film != null) {
            film.getLikes().remove(userId);
            filmStorage.updateFilm(filmId, film);
        } else {
            throw new NotFoundException("Film or User not found");
        }
    }

    public List<Film> getMostLikedFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        return filmStorage.findAllFilms().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(film -> film.getLikes().size())))
                .limit(count)
                .collect(Collectors.toList());
    }
}
