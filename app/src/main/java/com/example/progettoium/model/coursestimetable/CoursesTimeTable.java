package com.example.progettoium.model.coursestimetable;

import java.util.ArrayList;
import java.util.List;

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

    public CoursesTimeTable() {
    }

    public static ArrayList<CoursesTimeTable> createCourses() {
        ArrayList<CoursesTimeTable> allCourses = new ArrayList<CoursesTimeTable>();
        for (int i = 0; i < 15; i++){
            CoursesTimeTable course = new CoursesTimeTable();
            course.codCourse = i;
            course.startTime = ""+i;
            course.endTime = ""+(i+1);
            allCourses.add(course);
        }
        return allCourses;
    }
}
