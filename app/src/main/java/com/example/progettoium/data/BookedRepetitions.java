package com.example.progettoium.data;

public class BookedRepetitions {
    String day;
    String startTime;
    String title;
    String surname;
    String name;

    public BookedRepetitions(String day, String startTime, String title, String surname, String name) {
        this.day = day;
        this.startTime = startTime;
        this.title = title;
        this.surname = surname;
        this.name = name;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTitle() {
        return title;
    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }
}
