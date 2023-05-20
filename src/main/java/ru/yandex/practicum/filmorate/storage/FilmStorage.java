package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(int id, Film filmToUpdate);

    Film findFilmById(int id);

    void deleteFilm(int id);
}
