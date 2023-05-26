package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Component
public class FilmDaoImpl implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(Mpa.forValues(resultSet.getInt("MPA_FILM_RATING_ID")))
                .genres(getGenresByFilmId(resultSet.getInt("id")))
                .build();
    }

    private LinkedHashSet<Genre> getGenresByFilmId(int filmId) {
        String query = "SELECT g.genre_name FROM genre_films gf " +
                "JOIN genres g ON gf.genre_id = g.id " +
                "WHERE gf.film_id = ?";
        List<String> genreNames = jdbcTemplate.queryForList(query, String.class, filmId);
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        for (String genreName : genreNames) {
            for (Genre genre : Genre.values()) {
                if (genre.getName().equals(genreName)) {
                    genres.add(genre);
                    break;
                }
            }
        }
        return genres;
    }

    @Override
    public List<Film> findAllFilms() {
        String sqlQuery = "select id, name, description, release_date, duration, MPA_FILM_RATING_ID from films";
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

    private void addGenres(int filmId, Set<Genre> genres) {
        String sqlQuery = "INSERT INTO genre_films(genre_id, film_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, genre.getId(), filmId);
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

    private void deleteLikes(int id) {
        String sqlQuery = "DELETE FROM FILM_LIKES WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film updateFilm(int id, Film filmToUpdate) {
        deleteGenres(id);
        String insertGenreQuery = "INSERT INTO GENRE_FILMS(FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Genre genre : filmToUpdate.getGenres()) {
            jdbcTemplate.update(insertGenreQuery, id, genre.getId());
        }

        String updateQuery =
                "UPDATE FILMS " +
                        "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_film_rating_id = ? " +
                        "WHERE id = ?";
        jdbcTemplate.update(
                updateQuery,
                filmToUpdate.getName(),
                filmToUpdate.getDescription(),
                filmToUpdate.getReleaseDate(),
                filmToUpdate.getDuration(),
                filmToUpdate.getMpa().getId(),
                id
        );
        return findFilmById(id);
    }

    private void deleteGenres(int id) {
        String sqlQuery = "DELETE FROM GENRE_FILMS WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film findFilmById(int id) {
        String filmQuery = "SELECT COUNT(ID) FROM FILMS WHERE ID = ?";
        int count = jdbcTemplate.queryForObject(filmQuery, Integer.class, id);
        if (count == 0) {
            throw new NotFoundException("Film not found");
        }
        String sqlQuery = "select id, name, description, release_date, duration, MPA_FILM_RATING_ID " +
                "from films where id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public void deleteFilm(int id) {
        deleteGenres(id);
        deleteLikes(id);
        String sqlQuery = "DELETE FROM FILMS WHERE id = ?";
        int deletedRows = jdbcTemplate.update(sqlQuery, id);
        if (deletedRows > 0) {
            log.info("Film deleted {}", id);
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        return Arrays.asList(Genre.values());
    }

    @Override
    public Genre getGenreById(int id) {
        Genre genre = Genre.forValues(id);
        if (genre == null) {
            throw new NotFoundException("Genre not found");
        }
        return genre;
    }

    @Override
    public List<Mpa> getAllMpas() {
        return Arrays.asList(Mpa.values());
    }

    @Override
    public Mpa getMpaById(int id) {
        Mpa mpa = Mpa.forValues(id);
        if (mpa == null) {
            throw new NotFoundException("Mpa not found");
        }
        return mpa;
    }

    @Override
    public List<Film> getMostLikedFilms(Long count) {
        if (count == null) {
            count = 10L;
        }
        String query = "SELECT f.* FROM FILMS f " +
                "LEFT JOIN FILM_LIKES fl ON (f.ID = fl.FILM_ID) GROUP BY f.ID ORDER BY COUNT(fl.USER_ID) DESC LIMIT ?";
        return jdbcTemplate.query(query, this::mapRowToFilm, count);
    }
}
