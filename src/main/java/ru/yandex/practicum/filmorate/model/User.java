package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonIgnoreProperties(value = {"friends"})
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

    private Set<Integer> friends;

    public String getName() {
        if (name == null || name.trim().isEmpty()) {
            name = login;
        }
        return name;
    }

    public Set<Integer> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }
        return friends;
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", name='" + getName() + '\'' +
                ", birthday=" + birthday +
                ", friends=" + getFriends().size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday) && Objects.equals(friends, user.friends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday, friends);
    }
}
