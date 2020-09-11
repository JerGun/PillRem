package com.example.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

public class AppointmentBootReceiver extends BroadcastReceiver {
    private String mTime;
    private String mDate;
    private String mActive;
    private String[] mDateSplit;
    private String[] mTimeSplit;
    private int mYear, mMonth, mHour, mMinute, mDay, mReceivedID;

    private Calendar mCalendar;
    private AppointmentReceiver mAlarmReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            AppointmentDatabase rb = new AppointmentDatabase(context);
            mCalendar = Calendar.getInstance();
            mAlarmReceiver = new AppointmentReceiver();

            List<Appointment> appointments = rb.getAllAppointments();

            for (Appointment rm : appointments) {
                mReceivedID = rm.getID();
                mActive = rm.getActive();
                mDate = rm.getDate();
                mTime = rm.getTime();

                mDateSplit = mDate.split("/");
                mTimeSplit = mTime.split(":");

                mDay = Integer.parseInt(mDateSplit[0]);
                mMonth = Integer.parseInt(mDateSplit[1]);
                mYear = Integer.parseInt(mDateSplit[2]);
                mHour = Integer.parseInt(mTimeSplit[0]);
                mMinute = Integer.parseInt(mTimeSplit[1]);

                mCalendar.set(Calendar.MONTH, --mMonth);
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);
            }
        }
    }
}
