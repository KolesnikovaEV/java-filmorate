package ru.yandex.practicum.filmorate.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    @ResponseBody
    public Film addFilm(@RequestBody @Valid Film film, BindingResult result) {
        log.info("Adding new film: {}", film);
        if (result.hasErrors()) {
            throw new ValidationException("Invalid film data");
        }
        film.setId(films.size() + 1);
        films.add(film);
        log.info("New film added: {}", film);
        return film;
    }

    @PutMapping
    @ResponseBody
    public Film updateFilm(@RequestBody @Valid Film filmToUpdate,
                           BindingResult result) {
        log.info("Updating film: {}", filmToUpdate);

        if (result.hasErrors()) {
            throw new ValidationException("Invalid film data");
        }
        Optional<Film> optionalFilm = films.stream()
                .filter(user -> user.getId() == filmToUpdate.getId())
                .findFirst();

        if (optionalFilm.isPresent()) {
            Film existingFilm = optionalFilm.get();
            existingFilm.setName(filmToUpdate.getName());
            existingFilm.setDescription(filmToUpdate.getDescription());
            existingFilm.setReleaseDate(filmToUpdate.getReleaseDate());
            existingFilm.setDuration(filmToUpdate.getDuration());

            log.info("Film updated: {}", existingFilm);
            return existingFilm;
        } else {
            throw new ValidationException("Film not found");
        }
    }
}
