package ru.yandex.practicum.filmorate.model;

public class Mpa {
    private int id;
    private String name;

    public Mpa() {
    }

    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
