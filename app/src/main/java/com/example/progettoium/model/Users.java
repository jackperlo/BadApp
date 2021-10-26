package com.example.progettoium.model;

public class Users {
    String name;
    String email;

    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
