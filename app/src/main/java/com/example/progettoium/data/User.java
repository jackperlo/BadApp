package com.example.progettoium.data;

public class User {
    String account;
    String pwd;
    String role;
    String name;
    String surname;

    //user used for Registration
    public User(String account, String pwd, String role, String name, String surname) {
        this.account = account;
        this.pwd = pwd;
        this.role = role;
        this.name = name;
        this.surname = surname;
    }

    public User() {

    }

    //user used for Login
    public User(String account, String name, String surname) {
        this.account = account;
        this.name = name;
        this.surname = surname;
    }

    public String getAccount() {
        return account;
    }

    public String getPwd() {
        return pwd;
    }

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

}
