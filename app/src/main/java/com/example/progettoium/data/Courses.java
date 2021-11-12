package com.example.progettoium.data;

import java.util.ArrayList;

public class Courses {
    int IDCourse;
    String title;
    ArrayList<Teachers> teachersList;

    public Courses(int IDCourse, String title) {
        this.IDCourse = IDCourse;
        this.title = title;
    }

    public Courses(int IDCourse, String title, ArrayList<Teachers> teachersList) {
        this.IDCourse = IDCourse;
        this.title = title;
        this.teachersList = teachersList;
    }

    public int getIDCourse() {
        return IDCourse;
    }

    public String getTitle() { return title; }

    public ArrayList<Teachers> getTeachersList() { return teachersList; }

    //used by the spinner to show the course title
    @Override
    public String toString() {
        return title;
    }
}
