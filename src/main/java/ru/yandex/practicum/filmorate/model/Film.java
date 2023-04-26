package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;


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
}
