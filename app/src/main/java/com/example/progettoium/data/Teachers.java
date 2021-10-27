package com.example.progettoium.data;

public class Teachers {
    int codTeacher;
    String mail;
    String name;
    String surname;

    public Teachers(int codTeacher, String mail, String name, String surname) {
        this.codTeacher = codTeacher;
        this.mail = mail;
        this.name = name;
        this.surname = surname;
    }

    public int getCodTeacher() {
        return codTeacher;
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
