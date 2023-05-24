package ru.yandex.practicum.filmorate.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody @Valid Film filmToUpdate) {
        return filmStorage.updateFilm(filmToUpdate.getId(), filmToUpdate);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int userId, @PathVariable int id) {
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int userId, @PathVariable int id) {
        filmService.removeLike(userId, id);
    }

    @GetMapping("/films/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false) Long count) {
        return filmService.getMostLikedFilms(count);
    }

    @PutMapping("/films/{id}")
    public Film updateFilm(@PathVariable int id, @RequestBody @Valid Film filmToUpdate) {
        filmToUpdate.setId(id);
        return filmStorage.updateFilm(id, filmToUpdate);
    }

    @GetMapping("/films/{filmId}")
    public Film getFilmById(@PathVariable int filmId) {
        Film film = filmStorage.findFilmById(filmId);
        return film;
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmStorage.deleteFilm(id);
    }
    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmStorage.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<Mpa> getAllMpas() {
        return filmStorage.getAllMpas();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return filmStorage.getMpaById(id);
    }
}
