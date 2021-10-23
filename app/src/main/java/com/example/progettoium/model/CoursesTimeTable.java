package com.example.progettoium.model;

public class CoursesTimeTable {
    String day;
    String startTime;
    String endTime;
    int codCourse;

    public CoursesTimeTable(String day, String startTime, String endTime, int codCourse) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.codCourse = codCourse;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getCodCourse() {
        return codCourse;
    }
}
