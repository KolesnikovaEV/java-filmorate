package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

@RunWith(SpringRunner.class)
public class FilmorateApplicationTests {

    @LocalServerPort
    private int port;
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
        this.headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
    }

    @Test
    public void testAddUser() {
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@test.com");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        newUser.setLogin("test_user");

        HttpEntity<User> entity = new HttpEntity<>(newUser, headers);

        ResponseEntity<User> response = restTemplate.exchange(
                "http://localhost:" + port + "/users",
                HttpMethod.POST, entity, User.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        User createdUser = response.getBody();

        Assertions.assertNotNull(createdUser.getId());
        Assertions.assertEquals(newUser.getName(), createdUser.getName());
        Assertions.assertEquals(newUser.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(newUser.getBirthday(), createdUser.getBirthday());
        Assertions.assertEquals(newUser.getLogin(), createdUser.getLogin());
    }

    @Test
    public void testUpdateUser() {
        // Создаем пользователя
        User newUser = new User();
        newUser.setName("Test User");
        newUser.setEmail("test@test.com");
        newUser.setBirthday(LocalDate.of(2000, 1, 1));
        newUser.setLogin("test_user");

        HttpEntity<User> createEntity = new HttpEntity<>(newUser, headers);

        ResponseEntity<User> createResponse = restTemplate.exchange(
                "http://localhost:" + port + "/users",
                HttpMethod.POST, createEntity, User.class);

        Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        User createdUser = createResponse.getBody();

        // Обновляем данные пользователя
        createdUser.setName("New Name");
        createdUser.setEmail("newemail@test.com");

        HttpEntity<User> updateEntity = new HttpEntity<>(createdUser, headers);

        ResponseEntity<User> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/users",
                HttpMethod.PUT, updateEntity, User.class);

        Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        User updatedUser = updateResponse.getBody();

        Assertions.assertNotNull(updatedUser.getId());
        Assertions.assertEquals(createdUser.getName(), updatedUser.getName());
        Assertions.assertEquals(createdUser.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(createdUser.getBirthday(), updatedUser.getBirthday());
        Assertions.assertEquals(createdUser.getLogin(), updatedUser.getLogin());
    }

    @Test
    public void testFindAll() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@test.com");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        user1.setLogin("user_1");
    }

//    @Test
//    public void testAddUserValidationError() {
//        User newUser = new User();
//        newUser.setName("");
//        newUser.setEmail("bademail.com");
//        newUser.setBirthday(LocalDate.of(2010, 1, 1));
//        newUser.setLogin("");
//
//        HttpEntity<User> entity = new HttpEntity<>(newUser, headers);
//
//        HttpStatusCodeException exception = Assertions.assertThrows(
//                HttpStatusCodeException.class,
//                () -> restTemplate.exchange(
//                        "http://localhost:" + port + "/users",
//                        HttpMethod.POST, entity, User.class)
//        );
//
//        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
//        Assertions.assertTrue(exception.getResponseBodyAsString()
//                .contains("name must not be blank"));
//        Assertions.assertTrue(exception.getResponseBodyAsString()
//                .contains("Email should be a valid email address"));
//        Assertions.assertTrue(exception.getResponseBodyAsString()
//                .contains("login must not be blank"));
//    }
//
//    @Test
//    public void testUpdateUserNotFound() {
//        User newUser = new User();
//        newUser.setId(1000);
//        newUser.setName("Test User");
//        newUser.setEmail("test@test.com");
//        newUser.setBirthday(LocalDate.of(2000, 1, 1));
//        newUser.setLogin("test_user");
//
//        HttpEntity<User> entity = new HttpEntity<>(newUser, headers);
//
//        HttpStatusCodeException exception = Assertions.assertThrows(
//                HttpStatusCodeException.class,
//                () -> restTemplate.exchange(
//                        "http://localhost:" + port + "/users",
//                        HttpMethod.PUT, entity, User.class)
//        );
//
//        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
//        Assertions.assertTrue(exception.getResponseBodyAsString()
//                .contains("User not found"));
//    }
}

