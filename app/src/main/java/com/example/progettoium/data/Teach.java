package com.example.progettoium.data;

public class Teach {
    int codTeacher;
    int codCourse;

    public Teach(int codTeacher, int codCourse) {
        this.codTeacher = codTeacher;
        this.codCourse = codCourse;
    }

    public int getCodTeacher() {
        return codTeacher;
    }

    public int getCodCourse() {
        return codCourse;
    }
}
