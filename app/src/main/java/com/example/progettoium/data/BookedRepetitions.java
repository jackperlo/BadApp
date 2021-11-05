package com.example.progettoium.data;

import java.util.ArrayList;

public class BookedRepetitions {
    String day;
    String startTime;
    int IDCourse;
    int IDTeacher;

    public BookedRepetitions(String day, String startTime, int IDCourse, int IDTeacher) {
        this.day = day;
        this.startTime = startTime;
        this.IDCourse = IDCourse;
        this.IDTeacher = IDTeacher;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getIDCourse() {
        return IDCourse;
    }

    public int getIDTeacher() {
        return IDTeacher;
    }
/*
    public static ArrayList<CoursesTimeTable> createCourses() {
        ArrayList<CoursesTimeTable> allCourses = new ArrayList<CoursesTimeTable>();
        for (int i = 0; i < 15; i++){
            CoursesTimeTable course = new CoursesTimeTable();
            course.IDCourse = i;
            course.startTime = ""+i;
            allCourses.add(course);
        }
        return allCourses;
    }*/
}
