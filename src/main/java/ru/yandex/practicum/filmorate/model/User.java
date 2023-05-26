package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
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

    private List<Integer> friends;
    private boolean friendshipConfirmed = false;

    public String getName() {
        if (name == null || name.trim().isEmpty()) {
            name = login;
        }
        return name;
    }

    public List<Integer> getFriends() {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        return friends;
    }

    public void setFriends(List<Integer> friends) {
        this.friends = friends;
    }

    public User(int id, String email, String login, String name, LocalDate birthday,
                List<Integer> friends, boolean friendshipConfirmed) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
        this.friendshipConfirmed = friendshipConfirmed;
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
                ", friendshipConfirmed=" + friendshipConfirmed +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday) && Objects.equals(friends, user.friends) && friendshipConfirmed == user.friendshipConfirmed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, login, name, birthday, friends, friendshipConfirmed);
    }
}
