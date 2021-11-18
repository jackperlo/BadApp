package com.example.progettoium.data;

public class Teachers {
    int IDTeacher;
    String mail;
    String name;
    String surname;
    String fullName;

    public Teachers(int IDTeacher, String name, String surname) {
        this.IDTeacher = IDTeacher;
        this.fullName = name + " " + surname;
    }

    public int getIDTeacher() {
        return IDTeacher;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getFullName(){ return fullName; }

    //used from the spinner to show the full names
    @Override
    public String toString() {
        return fullName;
    }
}
