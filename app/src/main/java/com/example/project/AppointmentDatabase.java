package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AppointmentDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "appointment.db";

    // Table name
    private static final String TABLE_NAME = "appointment";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DOCTOR = "doctor";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_ACTIVE = "active";

    public AppointmentDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_APPOINTMENTS_TABLE = "CREATE TABLE " + TABLE_NAME +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DOCTOR + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_ACTIVE + " BOOLEAN" + ")";
        db.execSQL(CREATE_APPOINTMENTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Adding new Reminder
    public int addAppointment(Appointment appointment){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE , appointment.getTitle());
        values.put(KEY_DOCTOR , appointment.getDoctor());
        values.put(KEY_LOCATION , appointment.getLocation());
        values.put(KEY_DATE , appointment.getDate());
        values.put(KEY_TIME , appointment.getTime());
        values.put(KEY_ACTIVE, appointment.getActive());

        // Inserting Row
        long ID = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single Reminder
    public Appointment getAppointment(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{
                KEY_ID,
                KEY_TITLE,
                KEY_DOCTOR,
                KEY_LOCATION,
                KEY_DATE,
                KEY_TIME,
                KEY_ACTIVE
        }, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Appointment appointment = new Appointment(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6));

        return appointment;
    }

    // Getting all Reminders
    public List<Appointment> getAllAppointments(){
        List<Appointment> appointmentList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Appointment appointment = new Appointment();
                appointment.setID(Integer.parseInt(cursor.getString(0)));
                appointment.setTitle(cursor.getString(1));
                appointment.setDoctor(cursor.getString(2));
                appointment.setLocation(cursor.getString(3));
                appointment.setDate(cursor.getString(4));
                appointment.setTime(cursor.getString(5));
                appointment.setActive(cursor.getString(6));

                // Adding Reminders to list
                appointmentList.add(appointment);
            } while (cursor.moveToNext());
        }
        return appointmentList;
    }

    // Getting Reminders Count
    public int getAppointmentsCount(){
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating single Reminder
    public int updateAppointment(Appointment appointment){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE , appointment.getTitle());
        values.put(KEY_DOCTOR , appointment.getDoctor());
        values.put(KEY_LOCATION , appointment.getLocation());
        values.put(KEY_DATE , appointment.getDate());
        values.put(KEY_TIME , appointment.getTime());
        values.put(KEY_ACTIVE, appointment.getActive());

        // Updating row
        return db.update(TABLE_NAME, values, KEY_ID + "=?",
                new String[]{String.valueOf(appointment.getID())});
    }

    // Deleting single Reminder
    public void deleteAppointment(Appointment appointment){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?",
                new String[]{String.valueOf(appointment.getID())});
        db.close();
    }
}
