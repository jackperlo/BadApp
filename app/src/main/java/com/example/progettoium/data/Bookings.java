package com.example.progettoium.data;

public class Bookings {
    int codTeacher;
    int codCourse;
    int codUser;
    String day;
    String startTime;
    String endTime;

    public Bookings(int codTeacher, int codCourse, int codUser, String day, String startTime, String endTime) {
        this.codTeacher = codTeacher;
        this.codCourse = codCourse;
        this.codUser = codUser;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getCodTeacher() {
        return codTeacher;
    }

    public int getCodCourse() {
        return codCourse;
    }

    public int getCodUser() {
        return codUser;
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
}
