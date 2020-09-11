package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String ADMIN_TABLE_NAME = "admin";
    public static final String USER_TABLE_NAME = "user";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String BLOOD = "blood";
    public static final String ALLERGIC = "allergic";
    public static final String TREATMENT = "treatment";
    public static final String EMERGENCY_NAME = "emergency_name";
    public static final String EMERGENCY_SURNAME = "emergency_surname";
    public static final String EMERGENCY_PHONE = "emergency_phone";
    public static final String STATE = "state";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + ADMIN_TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " VARCHAR, " +
                PASSWORD + " VARCHAR " +
                ");");

        sqLiteDatabase.execSQL("CREATE TABLE " + USER_TABLE_NAME + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME + " VARCHAR, " +
                PASSWORD + " VARCHAR, " +
                NAME + " VARCHAR, " +
                SURNAME + " VARCHAR, " +
                ADDRESS + " VARCHAR, " +
                PHONE + " VARCHAR(10), " +
                BLOOD + " VARCHAR(1), " +
                ALLERGIC + " VARCHAR, " +
                TREATMENT + " VARCHAR, " +
                EMERGENCY_NAME + " VARCHAR, " +
                EMERGENCY_SURNAME + " VARCHAR, " +
                EMERGENCY_PHONE + " VARCHAR(10), " +
                STATE + " VARCHAR " +
                ");");

        sqLiteDatabase.execSQL("INSERT INTO " + ADMIN_TABLE_NAME + " (" +
                USERNAME + ", " +
                PASSWORD +
                ") VALUES ('admin', 'admin');");

        sqLiteDatabase.execSQL("INSERT INTO " + USER_TABLE_NAME + " (" +
                USERNAME + ", " +
                PASSWORD + ", " +
                NAME + ", " +
                SURNAME + ", " +
                ADDRESS + ", " +
                PHONE + ", " +
                BLOOD + ", " +
                ALLERGIC + ", " +
                TREATMENT + ", " +
                EMERGENCY_NAME + ", " +
                EMERGENCY_SURNAME + ", " +
                EMERGENCY_PHONE + ", " +
                STATE +
                ") VALUES ('test', 'test', 'John', 'Smith', 'University of Phayao', '123456789', 'O', 'ไม่แพ้ยา', 'ไม่เคยรับการรักษา', 'Michael', 'Brown', '987654321', 'false');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ADMIN_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<Admin> adminViewAllData() {
        List<Admin> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + ADMIN_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(PASSWORD));

                Admin admin = new Admin(username, password);
                arrayList.add(admin);
            }
            while(cursor.moveToNext());
        }
        return arrayList;
    }

    public long adminAddData(Admin admin) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", admin.getUsername());
        contentValues.put("password", admin.getPassword());
        return sqLiteDatabase.insert(ADMIN_TABLE_NAME, null, contentValues);
    }

    public int adminDeleteData(String data) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(ADMIN_TABLE_NAME, "username = ?", new String[]{String.valueOf(data)});
    }

    public List<User> userViewAllData() {
        List<User> arrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        String query = "SELECT * FROM " + USER_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(PASSWORD));
                String name = cursor.getString(cursor.getColumnIndex(NAME));
                String surname = cursor.getString(cursor.getColumnIndex(SURNAME));
                String address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                String phone = cursor.getString(cursor.getColumnIndex(PHONE));
                String blood = cursor.getString(cursor.getColumnIndex(BLOOD));
                String allergic = cursor.getString(cursor.getColumnIndex(ALLERGIC));
                String treatment = cursor.getString(cursor.getColumnIndex(TREATMENT));
                String emergency_name = cursor.getString(cursor.getColumnIndex(EMERGENCY_NAME));
                String emergency_surname = cursor.getString(cursor.getColumnIndex(EMERGENCY_SURNAME));
                String emergency_phone = cursor.getString(cursor.getColumnIndex(EMERGENCY_PHONE));

                User user = new User(username, password, name, surname, address, phone, blood,
                        allergic, treatment, emergency_name, emergency_surname, emergency_phone);
                arrayList.add(user);
            }
            while(cursor.moveToNext());
        }
        return arrayList;
    }

    public Cursor userViewData(String username) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                null,
                USERNAME + " = ?",
                new String[]{username},
                null,
                null,
                null);

        return cursor;
    }

    public long userAddData(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user.getUsername());
        contentValues.put("password", user.getPassword());
        contentValues.put("name", user.getName());
        contentValues.put("surname", user.getSurname());
        contentValues.put("address", user.getAddress());
        contentValues.put("phone", user.getPhone());
        contentValues.put("blood", user.getBlood());
        contentValues.put("allergic", user.getAllergic());
        contentValues.put("treatment", user.getTreatment());
        contentValues.put("emergency_name", user.getEmergency_name());
        contentValues.put("emergency_surname", user.getEmergency_surname());
        contentValues.put("emergency_phone", user.getEmergency_phone());
        contentValues.put("state", user.getState());

        return sqLiteDatabase.insert(USER_TABLE_NAME, null, contentValues);
    }

    public int userUpdateData(User user, String username) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", user.getName());
        contentValues.put("surname", user.getSurname());
        contentValues.put("address", user.getAddress());
        contentValues.put("phone", user.getPhone());
        contentValues.put("blood", user.getBlood());
        contentValues.put("allergic", user.getAllergic());
        contentValues.put("treatment", user.getTreatment());
        contentValues.put("emergency_name", user.getEmergency_name());
        contentValues.put("emergency_surname", user.getEmergency_surname());
        contentValues.put("emergency_phone", user.getEmergency_phone());

        int count = sqLiteDatabase.update(USER_TABLE_NAME, contentValues, "username = ?", new String[]{String.valueOf(username)});
        return count;
    }

    public int userDeleteData(String data) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(USER_TABLE_NAME, "username = ?", new String[]{String.valueOf(data)});
    }

    public int checkUserLogin(String username, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                new String[] {ID},
                USERNAME + " = ? AND " +
                        PASSWORD + " = ?",
                new String[]{username, password},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }

    public int checkAdminLogin(String username, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ADMIN_TABLE_NAME,
                new String[] {ID},
                USERNAME + " = ? AND " +
                        PASSWORD + " = ?",
                new String[]{username, password},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }

    public int checkEmergencyCount(String name, String surname, String phone){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                new String[]{USERNAME},
                EMERGENCY_NAME + " LIKE ? AND " +
                EMERGENCY_SURNAME + " LIKE ? AND " +
                EMERGENCY_PHONE + " LIKE ?",
                new String[]{"%" + name + "%", "%" + surname + "%", "%" + phone + "%"},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }

    public Cursor checkEmergency(String name, String surname, String phone){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                null,
                EMERGENCY_NAME + " LIKE ? AND " +
                        EMERGENCY_SURNAME + " LIKE ? AND " +
                        EMERGENCY_PHONE + " LIKE ?",
                new String[]{"%" + name + "%", "%" + surname + "%", "%" + phone + "%"},
                null,
                null,
                null);

        return cursor;
    }

    public int userUpdateState(User user, String username) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("state", user.getState());

        int count = sqLiteDatabase.update(USER_TABLE_NAME, contentValues, "username = ?", new String[]{String.valueOf(username)});
        return count;
    }

    public int checkStateCount(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                new String[]{USERNAME},
                STATE + " = ? ",
                new String[]{"true"},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }

    public Cursor checkState(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                new String[]{USERNAME},
                STATE + " = ? ",
                new String[]{"true"},
                null,
                null,
                null);

        return cursor;
    }

    public int checkUserID(String username){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(USER_TABLE_NAME,
                new String[]{USERNAME},
                USERNAME + " = ? ",
                new String[]{username},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }

    public int checkAdminID(String username){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ADMIN_TABLE_NAME,
                new String[]{USERNAME},
                USERNAME + " = ? ",
                new String[]{username},
                null,
                null,
                null);

        int cursorCount = cursor.getCount();

        cursor.close();
        return cursorCount;
    }
}



