package com.example.progettoium.data;

public class Teach {
    int IDTeacher;
    int IDCourse;

    public Teach(int IDTeacher, int IDCourse) {
        this.IDTeacher = IDTeacher;
        this.IDCourse = IDCourse;
    }

    public int getIDTeacher() {
        return IDTeacher;
    }

    public int getIDCourse() {
        return IDCourse;
    }
}
