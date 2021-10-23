package com.example.progettoium.model;

public class Users {
    int codUser;
    String account;
    String password;
    String role;

    public Users(int codUser, String account, String password, String role) {
        this.codUser = codUser;
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public int getCodUser() {
        return codUser;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
