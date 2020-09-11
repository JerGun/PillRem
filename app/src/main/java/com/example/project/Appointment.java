package com.example.project;

public class Appointment {
    private int mID;
    private String mTitle;
    private String mDoctor;
    private String mLocation;
    private String mDate;
    private String mTime;
    private String mActive;


    public Appointment(int ID, String Title, String Doctor, String Location, String Date, String Time, String Active){
        mID = ID;
        mTitle = Title;
        mDoctor = Doctor;
        mLocation = Location;
        mDate = Date;
        mTime = Time;
        mActive = Active;
    }

    public Appointment(String Title, String Doctor, String Location, String Date, String Time, String Active){
        mTitle = Title;
        mDoctor = Doctor;
        mLocation = Location;
        mDate = Date;
        mTime = Time;
        mActive = Active;
    }

    public Appointment(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDoctor() {
        return mDoctor;
    }

    public void setDoctor(String doctor) {
        mDoctor = doctor;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }
}