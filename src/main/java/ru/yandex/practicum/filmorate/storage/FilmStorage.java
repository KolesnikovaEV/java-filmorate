package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(int id, Film filmToUpdate);

    Film findFilmById(int id);

    void deleteFilm(int id);

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Mpa> getAllMpas();

    Mpa getMpaById(int id);

    void addLikes(int userId, int filmId);

    List<Film> getMostLikedFilms(Long count);
}
