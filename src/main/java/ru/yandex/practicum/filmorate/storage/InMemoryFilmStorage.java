//package ru.yandex.practicum.filmorate.storage;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.exception.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Component
//public class InMemoryFilmStorage implements FilmStorage {
//    private final Map<Integer, Film> films = new HashMap<>();
//
//    @Override
//    public List<Film> findAllFilms() {
//        log.info("Finding all films");
//        log.info("Found {} films", films.size());
//        return new ArrayList<>(films.values());
//    }
//
//    @Override
//    public Film addFilm(Film film) {
//        log.info("Adding new film: {}", film);
//        try {
//            film.setId(films.size() + 1);
//            films.put(film.getId(), film);
//            log.info("New film added: {}", film);
//            return film;
//        } catch (ValidationException e) {
//            throw new ValidationException("Invalid film data");
//        }
//    }
//
//    @Override
//    public Film updateFilm(int id, Film filmToUpdate) {
//        log.info("Updating film: {}", filmToUpdate);
//
//        Film existingFilm = films.get(filmToUpdate.getId());
//        if (existingFilm != null) {
//            existingFilm.setName(filmToUpdate.getName());
//            existingFilm.setDescription(filmToUpdate.getDescription());
//            existingFilm.setReleaseDate(filmToUpdate.getReleaseDate());
//            existingFilm.setDuration(filmToUpdate.getDuration());
//
//            films.put(existingFilm.getId(), existingFilm);
//
//            log.info("Film updated: {}", existingFilm);
//            return existingFilm;
//        } else {
//            throw new NotFoundException("Film not found");
//        }
//    }
//
//    @Override
//    public Film findFilmById(int id) {
//        Film existingFilm = films.get(id);
//        if (existingFilm != null) {
//            return existingFilm;
//        } else {
//            throw new NotFoundException("Film not found");
//        }
//    }
//
//    @Override
//    public void deleteFilm(int id) {
//        log.info("Removing film {}", id);
//        Film existingFilm = films.get(id);
//        if (existingFilm != null) {
//            log.info("Film deleted {}", id);
//            films.remove(id);
//        } else {
//            throw new NotFoundException("Film not found");
//        }
//    }
//}
