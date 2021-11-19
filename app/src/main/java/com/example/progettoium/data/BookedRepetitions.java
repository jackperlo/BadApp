package com.example.progettoium.data;

public class BookedRepetitions {
    String day;
    String startTime;
    String title;
    String surname;
    String name;
    int idCourse;
    int idTeacher;

    public BookedRepetitions(String day, String startTime, String title, String surname, String name, int idCourse, int idTeacher) {
        this.day = day;
        this.startTime = startTime;
        this.title = title;
        this.surname = surname;
        this.name = name;
        this.idCourse = idCourse;
        this.idTeacher = idTeacher;
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

    public int getIdCourse() {
        return idCourse;
    }

    public int getIdTeacher() { return idTeacher; }
}
