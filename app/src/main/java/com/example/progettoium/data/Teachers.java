package com.example.progettoium.data;

public class Teachers {
    int IDTeacher;
    String mail;
    String name;
    String surname;

    public Teachers(int IDTeacher, String mail, String name, String surname) {
        this.IDTeacher = IDTeacher;
        this.mail = mail;
        this.name = name;
        this.surname = surname;
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
}
