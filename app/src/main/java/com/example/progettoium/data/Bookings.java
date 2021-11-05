package com.example.progettoium.data;

public class Bookings {
    int IDTeacher;
    int IDCourse;
    int account;
    String day;
    String startTime;
    String state;

    public Bookings(int IDTeacher, int IDCourse, int account, String day, String startTime, String state) {
        this.IDTeacher = IDTeacher;
        this.IDCourse = IDCourse;
        this.account = account;
        this.day = day;
        this.startTime = startTime;
        this.state = state;
    }

    public int getIDTeacher() {
        return IDTeacher;
    }

    public int getIDCourse() {
        return IDCourse;
    }

    public int getAccount() {
        return account;
    }

    public String getDay() {
        return day;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getState() {
        return state;
    }
}
