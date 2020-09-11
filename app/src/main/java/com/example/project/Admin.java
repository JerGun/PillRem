package com.example.project;

import java.io.Serializable;

public class Admin implements Serializable {

    int id;
    String username;
    String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        String showString = "Username: " + username + "\n" +
                "Password: " + password;
        return showString;
    }
}
