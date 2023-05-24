package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.InternalServerError;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
public class FilmDaoImpl implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();
    }
    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select id, name, description, release_date, duration from films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into FILMS (name, description, release_date, duration, MPA_FILM_RATING_ID) values (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            statement.setInt(5, film.getMpa().getId());

            return statement;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        addGenres(film.getId(), film.getGenres());

        log.info("Добавлен новый фильм: {}", film);

        return film;
    }
    private void addGenres(int filmId, List<Integer> genreIds) {
        String sqlQuery = "insert into genre_films(genre_id, film_id) values (?, ?)";
        for (Integer genreId : genreIds) {
            jdbcTemplate.update(sqlQuery, genreId, filmId);
        }
    }

    @Override
    public void addLikes(int userId, int filmId) {
        String filmQuery = "SELECT COUNT(USER_ID) FROM FILM_LIKES WHERE FILM_ID = ?";
        int count = jdbcTemplate.queryForObject(filmQuery, Integer.class, filmId);
        if (count == 0) {
            String sqlQuery = "insert into FILM_LIKES(USER_ID, FILM_ID) values (?, ?)";
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } else {
            throw new NotFoundException("Film not found or has like");
        }
    }

    @Override
    public Film updateFilm(int id, Film filmToUpdate) {
        String filmQuery = "SELECT COUNT(ID) FROM FILMS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(filmQuery, Integer.class, id);
        if (count == 0) {
            throw new NotFoundException("Film not found");
        }
        // получаем id рейтинга фильма в таблице MPA_FILM_RATING
        String ratingQuery = "SELECT ID FROM MPA_FILM_RATING WHERE MPA_FILM_RATING_NAME = ?";
        Integer ratingId = jdbcTemplate.queryForObject(ratingQuery, Integer.class, filmToUpdate.getMpa());

        // обновляем данные в таблице FILMS
        String updateFilmQuery = "UPDATE FILMS SET name = ?, description = ?, release_date = ?, duration = ?, MPA_FILM_RATING_ID = ? WHERE ID = ?";
        jdbcTemplate.update(updateFilmQuery,
                filmToUpdate.getName(),
                filmToUpdate.getDescription(),
                filmToUpdate.getReleaseDate(),
                filmToUpdate.getDuration(),
                ratingId,
                id);

        // удаляем старые связи жанров с фильмом в таблице GENRE_FILMS
        deleteGenres(id);

        // добавляем новые связи жанров с фильмом в таблицу GENRE_FILMS
        String insertGenreQuery = "MERGE INTO GENRE_FILMS(FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Integer> genres = filmToUpdate.getGenres();
        for (Integer genreId : genres) {
            jdbcTemplate.update(insertGenreQuery, id, genreId);
        }

        return filmToUpdate;
    }
    @Override
    public Film findFilmById(int id) {
        String filmQuery = "SELECT COUNT(ID) FROM FILMS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(filmQuery, Integer.class, id);
        if (count == 0) {
            throw new NotFoundException("Film not found");
        }
        String sqlQuery = "select id, name, description, release_date, duration " +
                "from films where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }
    @Override
    public void deleteFilm(int id) {
        String sqlQuery = "DELETE FROM FILMS WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sqlQuery, id);
        if (deletedRows > 0) {
            log.info("Film deleted {}", id);
        } else {
            throw new NotFoundException("Film not found");
        }
        deleteGenres(id);
        deleteLikes(id);
    }

    private void deleteGenres(int id) {
        String sqlQuery = "DELETE FROM GENRE_FILMS WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    private void deleteLikes(int id) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT id, genre_name FROM genres";
        return jdbcTemplate.query(sqlQuery, new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("genre_name");
                return new Genre(id, name);
            }
        });
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlCountQuery = "SELECT COUNT(*) FROM genres WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sqlCountQuery, Integer.class, id);
        if (count == 0) {
            throw new NotFoundException("Genre not found");
        }
        String sqlQuery = "SELECT genre_name FROM genres WHERE id = ?";
        try {
            String genreName = jdbcTemplate.queryForObject(sqlQuery, String.class, id);
            if (Objects.isNull(genreName)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found");
            } else {
                return new Genre(id, genreName);
            }
        } catch (NotFoundException ex) {
            throw new NotFoundException("Genre not found");
        }
    }

    @Override
    public List<Mpa> getAllMpas() {
        String sqlQuery = "SELECT id, MPA_FILM_RATING_NAME FROM MPA_FILM_RATING";
        return jdbcTemplate.query(sqlQuery, new RowMapper<Mpa>() {
            @Override
            public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("id");
                String name = rs.getString("MPA_FILM_RATING_NAME");
                return new Mpa(id, name);
            }
        });
    }

    @Override
    public Mpa getMpaById(int id) {
        String sqlCountQuery = "SELECT COUNT(*) FROM mpa_film_rating WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sqlCountQuery, Integer.class, id);
        if (count == 0) {
            throw new NotFoundException("Mpa not found");
        }
        String sqlQuery = "SELECT mpa_film_rating_name FROM mpa_film_rating WHERE id = ?";
        try {
            String mpaName = jdbcTemplate.queryForObject(sqlQuery, String.class, id);
            if (Objects.isNull(mpaName)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mpa not found");
            } else {
                return new Mpa(id, mpaName);
            }
        } catch (NotFoundException ex) {
            throw new NotFoundException("Mpa not found");
        }
    }
}
