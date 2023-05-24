package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private int id;
    @NotBlank(message = "The name cannot be empty")
    private String name;

    @Size(max = 200, message = "The description must be no more than 200 characters")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Release date cannot be null")
    @PastOrPresent(message = "Release date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @AssertTrue(message = "Film must be released after 28 Dec 1895")
    public boolean isReleasedAfter1895() {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(minDate) || releaseDate.isEqual(minDate);
    }

    @Positive(message = "The duration of the film must be positive")
    private int duration;

    private List<Integer> genres;

    private Mpa mpa;

    private Set<Integer> likes;

    public Set<Integer> getLikes() {
        if (likes == null) {
            likes = new LinkedHashSet<>();
        }
        return likes;
    }

    public List<Integer> getGenres() {
        if (genres == null) {
            genres = new ArrayList<>();
        }
        return genres;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration,
                List<Integer> genres, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, List<Integer> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
    }
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", genres=" + genres +
                ", rating='" + mpa + '\'' +
                ", likes=" + likes +
                '}';
    }
}
