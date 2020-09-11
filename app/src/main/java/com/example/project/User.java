package com.example.project;

import java.io.Serializable;

public class User implements Serializable {

    int id;
    String username;
    String password;
    String name;
    String surname;
    String address;
    String phone;
    String blood;
    String allergic;
    String treatment;
    String emergency_name;
    String emergency_surname;
    String emergency_phone;
    String state;

    public User(String state) {
        this.state = state;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String name, String surname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }


    public User(String username, String password, String name, String last_name, String address, String phone
            , String blood, String allergic, String treatment, String emergency_name, String emergency_surname, String emergency_phone) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = last_name;
        this.address = address;
        this.phone = phone;
        this.blood = blood;
        this.allergic = allergic;
        this.treatment = treatment;
        this.emergency_name = emergency_name;
        this.emergency_surname = emergency_surname;
        this.emergency_phone = emergency_phone;
    }

    public User(String name, String last_name, String address, String phone, String blood
            , String allergic, String treatment, String emergency_name, String emergency_surname, String emergency_phone) {
        this.name = name;
        this.surname = last_name;
        this.address = address;
        this.phone = phone;
        this.blood = blood;
        this.allergic = allergic;
        this.treatment = treatment;
        this.emergency_name = emergency_name;
        this.emergency_surname = emergency_surname;
        this.emergency_phone = emergency_phone;
    }

    public User(String username, String password, String name, String last_name, String address, String phone, String blood
            , String allergic, String treatment, String emergency_name, String emergency_surname, String emergency_phone, String state) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = last_name;
        this.address = address;
        this.phone = phone;
        this.blood = blood;
        this.allergic = allergic;
        this.treatment = treatment;
        this.emergency_name = emergency_name;
        this.emergency_surname = emergency_surname;
        this.emergency_phone = emergency_phone;
        this.state = state;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getAllergic() {
        return allergic;
    }

    public void setAllergic(String allergic) {
        this.allergic = allergic;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getEmergency_name() {
        return emergency_name;
    }

    public void setEmergency_name(String emergency_name) {
        this.emergency_name = emergency_name;
    }

    public String getEmergency_surname() {
        return emergency_surname;
    }

    public void setEmergency_surname(String emergency_surname) {
        this.emergency_surname = emergency_surname;
    }

    public String getEmergency_phone() {
        return emergency_phone;
    }

    public void setEmergency_phone(String emergency_phone) {
        this.emergency_phone = emergency_phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        String showString = "Username: " + username + "\n" +
                "Password: " + password + "\n" +
                "Name: " + name + "\n" +
                "Last name: " + surname;
        return showString;
    }
}

