//package ru.yandex.practicum.filmorate;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import ru.yandex.practicum.filmorate.control.FilmController;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//
//import javax.validation.*;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//public class FilmControllerTest {
//    private FilmStorage filmStorage;
//    private UserStorage userStorage;
//    private FilmService filmService;
//    private FilmController filmController;
//    private static Validator validator;
//
//    @BeforeEach
//    public void setUp() {
//        filmStorage = new InMemoryFilmStorage();
//        userStorage = new InMemoryUserStorage();
//        filmService = new FilmService(filmStorage, userStorage);
//        filmController = new FilmController(filmService, filmStorage);
//
//        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
//        validator = validatorFactory.getValidator();
//    }
//
//    @Test
//    public void shouldGetListOfFilms() {
//        Film film = new Film(1, "name", "description",
//                LocalDate.of(1996, 12, 22), 120);
//        Film film2 = new Film(2, "name2", "description2",
//                LocalDate.of(1995, 12, 22), 120);
//        filmController.addFilm(film);
//        filmController.addFilm(film2);
//
//        Assertions.assertEquals(List.of(film, film2), filmController.findAll());
//    }
//
//    @Test
//    public void shouldUpdateFilm() {
//        Film film = new Film(1, "name", "description",
//                LocalDate.of(1996, 12, 22), 120);
//        Film film2 = new Film(1, "name2", "description2",
//                LocalDate.of(1995, 12, 22), 120);
//        filmController.addFilm(film);
//        filmController.updateFilm(film2);
//
//        Assertions.assertEquals(List.of(film2), filmController.findAll());
//    }
//
//    @Test
//    public void shouldNotUpdateFilm() {
//        Film film = new Film(1, "name", "description",
//                LocalDate.of(1996, 12, 22), 120);
//        Film updateFilm = new Film(9999, "name2", "description2",
//                LocalDate.of(1995, 12, 22), 120);
//        filmController.addFilm(film);
//
//        Exception exception = assertThrows(NotFoundException.class, () -> filmController.updateFilm(updateFilm));
//        assertEquals("Film not found", exception.getMessage());
//
//    }
//
//    @Test
//    public void shouldNotAddFilmWithEmptyName() {
//        Film film = new Film(1, "", "description",
//                LocalDate.of(1996, 12, 22), 120);
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> {
//            Set<ConstraintViolation<Film>> validates = validator.validate(film);
//            if (!validates.isEmpty()) {
//                throw new ConstraintViolationException(validates);
//            }
//        });
//    }
//
//
//    @Test
//    public void shouldNotAddFilmWithDescriptionMoreThan200Signs() {
//        Film film = new Film(1, "", "Lisa, a gambler who lives for the cards. Robert, a millionaire " +
//                "philanthropist, Mark (Elias Koteas), a cop reaching the end of a gruesome career. Zach, a brilliant " +
//                "psychiatrist. Melody, a teenager battling drug addictions. Diane, a caring nurse whose patients are the" +
//                " only life surrounding her. What do these six people have in common? Something connects them all - each" +
//                " of them is on the road to self-destruction, each is truly lost. And then, something happens: they all " +
//                "wake up in cells in a surreal facility, without knowing how they got there or why. Are they in denial? " +
//                "Are they crazy? What happened? They soon discover they are not alone, when the mysterious Jacob " +
//                "(Pyper-Ferguson) forces them into a disturbing experiment, during which the unwilling participants come" +
//                " to face disturbing truths about themselves and decide each others fate in a nerve racking game of dice. ",
//                LocalDate.of(1996, 12, 22), 120);
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> {
//            Set<ConstraintViolation<Film>> validates = validator.validate(film);
//            if (!validates.isEmpty()) {
//                throw new ConstraintViolationException(validates);
//            }
//        });
//    }
//
//    @Test
//    public void shouldNotAddFilmWithWrongDate() {
//        Film film = new Film(1, "name", "description",
//                LocalDate.of(1894, 12, 22), 120);
//
//        ValidationException exception = assertThrows(ValidationException.class, () -> {
//            Set<ConstraintViolation<Film>> validates = validator.validate(film);
//            if (!validates.isEmpty()) {
//                throw new ConstraintViolationException(validates);
//            }
//        });
//    }
//
//}
