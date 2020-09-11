package com.example.project;

public class Alarm {
    private int mID;
    private String mTitle;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;

    public Alarm(String mTitle, String mDate, String mTime, String mRepeat, String mRepeatNo, String mRepeatType, String mActive) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mRepeat = mRepeat;
        this.mRepeatNo = mRepeatNo;
        this.mRepeatType = mRepeatType;
        this.mActive = mActive;
    }

    public Alarm(int mID, String mTitle, String mDate, String mTime, String mRepeat, String mRepeatNo, String mRepeatType, String mActive) {
        this.mID = mID;
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mRepeat = mRepeat;
        this.mRepeatNo = mRepeatNo;
        this.mRepeatType = mRepeatType;
        this.mActive = mActive;
    }

    public Alarm(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        this.mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String Title) {
        this.mTitle = Title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String Date) {
        this.mDate = Date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String Time) {
        this.mTime = Time;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String Repeat) {
        this.mRepeat = Repeat;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String RepeatNo) {
        this.mRepeatNo = RepeatNo;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String RepeatType) {
        this.mRepeatType = RepeatType;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String Active) {
        this.mActive = Active;
    }
}
