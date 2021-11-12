package com.example.progettoium.data;

import java.util.ArrayList;

public class FreeRepetitions {
    String day;
    String startTime;
    ArrayList<Courses> coursesList;
    ArrayList<Teachers> teachersList;

    public FreeRepetitions(String day, String startTime, ArrayList<Courses> coursesList, ArrayList<Teachers> teachersList) {
        this.day = day;
        this.startTime = startTime;
        this.coursesList = coursesList;
        this.teachersList = teachersList;
    }

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

    public ArrayList<Teachers> getTeachersList() {
        return teachersList;
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
