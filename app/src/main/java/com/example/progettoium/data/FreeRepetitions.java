package com.example.progettoium.data;

import java.util.ArrayList;

public class FreeRepetitions {
    String day;
    String startTime;
    ArrayList<Courses> coursesList;

    public FreeRepetitions(String day, String startTime, ArrayList<Courses> coursesList) {
        this.day = day;
        this.startTime = startTime;
        this.coursesList = coursesList;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public ArrayList<Courses> getCoursesList() {
        return coursesList;
    }

}
