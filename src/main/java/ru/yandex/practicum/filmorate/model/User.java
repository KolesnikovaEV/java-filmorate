package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int id;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Login cannot be blank")
    private String login;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Birthday cannot be null")
    @PastOrPresent(message = "Birthday cannot be in the future")
    private LocalDate birthday;

    public String getName() {
        if (name == null || name.trim().isEmpty()) {
            name = login;
        }
        return name;
    }
}
