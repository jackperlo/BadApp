package com.example.progettoium.data;

public class Courses {
    int codCourse;
    String title;

    public Courses(int codCourse, String title) {
        this.codCourse = codCourse;
        this.title = title;
    }

    public int getCodCourse() {
        return codCourse;
    }

    public String getTitle() { return title; }

}
