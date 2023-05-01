package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final Map<Integer, Film> films = new HashMap<>();

    public List<Film> findAllFilms() {
        log.info("Finding all films");
        log.info("Found {} films", films.size());
        return new ArrayList<>(films.values());
    }

    public Film addFilm(Film film) {
        log.info("Adding new film: {}", film);
        try {
            film.setId(films.size() + 1);
            films.put(film.getId(), film);
            log.info("New film added: {}", film);
            return film;
        } catch (ValidationException e) {
            throw new ValidationException("Invalid film data");
        }
    }

    public Film updateFilm(Film filmToUpdate) {
        log.info("Updating film: {}", filmToUpdate);

        Film existingFilm = films.get(filmToUpdate.getId());
        if (existingFilm != null) {
            existingFilm.setName(filmToUpdate.getName());
            existingFilm.setDescription(filmToUpdate.getDescription());
            existingFilm.setReleaseDate(filmToUpdate.getReleaseDate());
            existingFilm.setDuration(filmToUpdate.getDuration());

            films.put(existingFilm.getId(), existingFilm);

            log.info("Film updated: {}", existingFilm);
            return existingFilm;
        } else {
            throw new NotFoundException("Film not found");
        }
    }
}
