package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "alarm.db";

    // Table name
    private static final String TABLE_NAME = "AlarmTable";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_REPEAT_NO = "repeat_no";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_ACTIVE = "active";

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_REPEAT + " BOOLEAN,"
                + KEY_REPEAT_NO + " INTEGER,"
                + KEY_REPEAT_TYPE + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN" + ")";
        db.execSQL(CREATE_ALARMS_TABLE);
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

    // Adding new Alarm
    public int addAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE , alarm.getTitle());
        values.put(KEY_DATE , alarm.getDate());
        values.put(KEY_TIME , alarm.getTime());
        values.put(KEY_REPEAT , alarm.getRepeat());
        values.put(KEY_REPEAT_NO , alarm.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, alarm.getRepeatType());
        values.put(KEY_ACTIVE, alarm.getActive());

        // Inserting Row
        long ID = db.insert(TABLE_NAME, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single Reminder
    public Alarm getAlarm(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{
                KEY_ID,
                KEY_TITLE,
                KEY_DATE,
                KEY_TIME,
                KEY_REPEAT,
                KEY_REPEAT_NO,
                KEY_REPEAT_TYPE,
                KEY_ACTIVE
                }, KEY_ID + "=?", new String[] {String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();
        Alarm alarm = new Alarm(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7));
        return alarm;
    }

    // Getting all Reminders
    public List<Alarm> getAllAlarms(){
        List<Alarm> alarmList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Alarm alarm = new Alarm();
                alarm.setID(Integer.parseInt(cursor.getString(0)));
                alarm.setTitle(cursor.getString(1));
                alarm.setDate(cursor.getString(2));
                alarm.setTime(cursor.getString(3));
                alarm.setRepeat(cursor.getString(4));
                alarm.setRepeatNo(cursor.getString(5));
                alarm.setRepeatType(cursor.getString(6));
                alarm.setActive(cursor.getString(7));

                // Adding Reminders to list
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }
        return alarmList;
    }

    // Getting Reminders Count
    public int getAlarmsCount(){
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);
        cursor.close();

        return cursor.getCount();
    }

    // Updating single Reminder
    public int updateAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE , alarm.getTitle());
        values.put(KEY_DATE , alarm.getDate());
        values.put(KEY_TIME , alarm.getTime());
        values.put(KEY_REPEAT , alarm.getRepeat());
        values.put(KEY_REPEAT_NO , alarm.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, alarm.getRepeatType());
        values.put(KEY_ACTIVE, alarm.getActive());

        // Updating row
        return db.update(TABLE_NAME, values, KEY_ID + "=?",
                new String[]{String.valueOf(alarm.getID())});
    }

    // Deleting single Reminder
    public void deleteAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=?",
                new String[]{String.valueOf(alarm.getID())});
        db.close();
    }
}
