package com.example.progettoium.data;

public class Courses {
    int IDCourse;
    String title;

    public Courses(int IDCourse, String title) {
        this.IDCourse = IDCourse;
        this.title = title;
    }

    public int getIDCourse() {
        return IDCourse;
    }

    public String getTitle() { return title; }

}
