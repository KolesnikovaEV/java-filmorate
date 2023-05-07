package ru.yandex.practicum.filmorate.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmStorage.findAllFilms();
    }

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film filmToUpdate) {
        return filmStorage.updateFilm(filmToUpdate.getId(), filmToUpdate);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int userId, @PathVariable int id) {
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int userId, @PathVariable int id) {
        filmService.removeLike(userId, id);
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(@RequestParam(required = false) Long count) {
        return filmService.getMostLikedFilms(count);
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @RequestBody @Valid Film filmToUpdate) {
        filmToUpdate.setId(id);
        return filmStorage.updateFilm(id, filmToUpdate);
    }

    @GetMapping("/{filmId}")
    public Film getUserById(@PathVariable int filmId) {
        Film film = filmStorage.findFilmById(filmId);
        return film;
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmStorage.deleteFilm(id);
    }
}
