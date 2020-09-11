package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class AppointmentEditActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{
    private EditText mTitleText, mDoctorText, mLocationText;
    private TextView mDateText, mTimeText;
    private FloatingActionButton mFAB1, mFAB2;
    private Switch mRepeatSwitch;
    private String mTitle, mDoctor, mLocation, mTime, mDate, mActive;
    private String[] mDateSplit, mTimeSplit;
    private int mReceivedID;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private Calendar mCalendar;
    private Appointment mReceivedAppointment;
    private AppointmentDatabase appointmentDatabase;
    private AppointmentReceiver appointmentReceiver;
    private AppointmentReceiverBefore appointmentReceiverBefore;

    // Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_DOCTOR = "doctor_key";
    private static final String KEY_LOCATION = "location_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_ACTIVE = "active_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_add);

        // Set Toolbar
        getSupportActionBar().setTitle("แก้ไขแจ้งเตือนการนัดหมาย");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Views
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDoctorText = (EditText) findViewById(R.id.input_doctor);
        mLocationText = (EditText) findViewById(R.id.input_location);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mDoctorText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDoctor = s.toString().trim();
                mDoctorText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mLocationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLocation = s.toString().trim();
                mLocationText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Get reminder id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        // Get reminder using reminder id
        appointmentDatabase = new AppointmentDatabase(this);
        mReceivedAppointment = appointmentDatabase.getAppointment(mReceivedID);

        // Get values from reminder
        mTitle = mReceivedAppointment.getTitle();
        mDoctor = mReceivedAppointment.getDoctor();
        mLocation = mReceivedAppointment.getLocation();
        mDate = mReceivedAppointment.getDate();
        mTime = mReceivedAppointment.getTime();
        mActive = mReceivedAppointment.getActive();

        // Setup TextViews using reminder values
        mTitleText.setText(mTitle);
        mDoctorText.setText(mDoctor);
        mLocationText.setText(mLocation);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedDoctor = savedInstanceState.getString(KEY_DOCTOR);
            mDoctorText.setText(savedDoctor);
            mDoctor = savedDoctor;

            String savedLocation = savedInstanceState.getString(KEY_LOCATION);
            mLocationText.setText(savedLocation);
            mLocation = savedLocation;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup up active buttons
        if (mActive.equals("false")) {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);
        }else if(mActive.equals("true")) {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        }

        // Obtain Date and Time details
        mCalendar = Calendar.getInstance();
        appointmentReceiver = new AppointmentReceiver();
        appointmentReceiverBefore = new AppointmentReceiverBefore();

        mDateSplit = mDate.split("/");
        mTimeSplit = mTime.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_DOCTOR, mDoctorText.getText());
        outState.putCharSequence(KEY_LOCATION, mLocationText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // On clicking Time picker
    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    // Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

    // On clicking the active button
    public void selectFab1(View v) {
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.GONE);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.VISIBLE);
        mActive = "true";
    }

    // On clicking the inactive button
    public void selectFab2(View v) {
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.GONE);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.VISIBLE);
        mActive = "false";
    }

    // On clicking the update button
    public void updateAppointment(){
        // Set new values in the reminder
        mReceivedAppointment.setTitle(mTitle);
        mReceivedAppointment.setDoctor(mDoctor);
        mReceivedAppointment.setLocation(mLocation);
        mReceivedAppointment.setDate(mDate);
        mReceivedAppointment.setTime(mTime);
        mReceivedAppointment.setActive(mActive);

        // Update reminder
        appointmentDatabase.updateAppointment(mReceivedAppointment);

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Cancel existing notification of the reminder by using its ID
        appointmentReceiver.cancelAlarm(getApplicationContext(), mReceivedID);

        // Create a new notification
        if (mActive.equals("true")) {
            appointmentReceiver.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
            appointmentReceiverBefore.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
        }
        // Create toast to confirm update
        Toast.makeText(getApplicationContext(), "แก้ไขเสร็จสิ้น", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_alarm_menu, menu);
        return true;
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.action_save:
                mTitleText.setText(mTitle);
                mDoctorText.setText(mDoctor);
                mLocationText.setText(mLocation);
                if(checkEmpty()) {
                    updateAppointment();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("ลบนัดหมาย");
                builder.setMessage("ต้องการลบนัดหมายใช่หรือไม่?");
                builder.setPositiveButton("ใช่", (dialog, which) -> {
                    // Delete reminder
                    appointmentDatabase.deleteAppointment(mReceivedAppointment);
                    // Delete reminder alarm
                    appointmentReceiverBefore.cancelAlarm(getApplicationContext(), mReceivedID);
                    appointmentReceiver.cancelAlarm(getApplicationContext(), mReceivedID);
                    Toast.makeText(getApplicationContext(), "ลบเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                });
                builder.setNegativeButton("ไม่", null);
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkEmpty(){
        if (TextUtils.isEmpty(mTitleText.getText())){
            mTitleText.setError("กรุณากรอกรายละเอียดการนัด");
            return false;
        }
        if(TextUtils.isEmpty(mDoctorText.getText())){
            mDoctorText.setError("กรุณากรอกชื่อแพทย์");
            return false;
        }
        if(TextUtils.isEmpty(mLocationText.getText())){
            mLocationText.setError("กรุณากรอกสถานที่");
            return false;
        }
        return true;
    }
}
