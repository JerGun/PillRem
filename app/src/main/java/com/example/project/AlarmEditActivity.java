package com.example.project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class AlarmEditActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB1, mFAB2;
    private Switch mRepeatSwitch;
    private String mTitle, mTime, mDate, mRepeat, mRepeatNo, mRepeatType, mActive;
    private String[] mDateSplit, mTimeSplit;
    private int mReceivedID;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Calendar mCalendar;
    private Alarm mReceivedAlarm;
    private AlarmDatabaseHelper alarmDatabase;
    private AlarmReceiverBefore mAlarmReceiverBefore;
    private AlarmReceiverBefore30 mAlarmReceiverBefore30;
    private AlarmReceiver mAlarmReceiver;

    // Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";

    // Constant values in milliseconds
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_add);

        // Set Toolbar
        getSupportActionBar().setTitle("แก้ไขแจ้งเตือนกินยา");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Views
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
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

        // Get reminder id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        // Get reminder using reminder id
        alarmDatabase = new AlarmDatabaseHelper(this);
        mReceivedAlarm = alarmDatabase.getAlarm(mReceivedID);

        // Get values from reminder
        mTitle = mReceivedAlarm.getTitle();
        mDate = mReceivedAlarm.getDate();
        mTime = mReceivedAlarm.getTime();
        mRepeat = mReceivedAlarm.getRepeat();
        mRepeatNo = mReceivedAlarm.getRepeatNo();
        mRepeatType = mReceivedAlarm.getRepeatType();
        mActive = mReceivedAlarm.getActive();

        // Setup TextViews using reminder values
        mTitleText.setText(mTitle);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("ทุกๆ " + mRepeatNo + " " + mRepeatType);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

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

        // Setup repeat switch
        if (mRepeat.equals("false")) {
            mRepeatSwitch.setChecked(false);
            mRepeatText.setText("ไม่ตั้งซ้ำ");
        }else if(mRepeat.equals("true")) {
            mRepeatSwitch.setChecked(true);
        }

        // Obtain Date and Time details
        mCalendar = Calendar.getInstance();
        mAlarmReceiverBefore = new AlarmReceiverBefore();
        mAlarmReceiverBefore30 = new AlarmReceiverBefore30();
        mAlarmReceiver = new AlarmReceiver();

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
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
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
        tpd.setThemeDark(false);
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

    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("ทุกๆ " + mRepeatNo + " " + mRepeatType);

        } else {
            mRepeat = "false";
            mRepeatText.setText("ไม่ตั้งซ้ำ");
        }
    }

    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = new String[3];

        items[0] = "ชั่วโมง";
        items[1] = "วัน";
        items[2] = "สัปดาห์";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ประเภทการตั้งซ้ำ");
        builder.setItems(items, (dialog, item) -> {

            mRepeatType = items[item];
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText("ทุกๆ " + mRepeatNo + " " + mRepeatType);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // On clicking repeat interval button
    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("จำนวนการตั้งซ้ำ");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("บันทึก",
                (dialog, whichButton) -> {

                    if (input.getText().toString().length() == 0) {
                        mRepeatNo = Integer.toString(1);
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatText.setText("ทุกๆ " + mRepeatNo + " " + mRepeatType);
                    }
                    else {
                        mRepeatNo = input.getText().toString().trim();
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatText.setText("ทุกๆ " + mRepeatNo + " " + mRepeatType);
                    }
                });
        alert.setNegativeButton("ยกเลิก", (dialog, whichButton) -> {
            // Do nothing
        });
        alert.show();
    }

    // On clicking the update button
    public void updateReminder(){
        // Set new values in the reminder
        mReceivedAlarm.setTitle(mTitle);
        mReceivedAlarm.setDate(mDate);
        mReceivedAlarm.setTime(mTime);
        mReceivedAlarm.setRepeat(mRepeat);
        mReceivedAlarm.setRepeatNo(mRepeatNo);
        mReceivedAlarm.setRepeatType(mRepeatType);
        mReceivedAlarm.setActive(mActive);

        // Update reminder
        alarmDatabase.updateAlarm(mReceivedAlarm);

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Cancel existing notification of the reminder by using its ID
        mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);
        mAlarmReceiverBefore.cancelAlarm(getApplicationContext(), mReceivedID);
        mAlarmReceiverBefore30.cancelAlarm(getApplicationContext(), mReceivedID);

        // Check repeat type
        if (mRepeatType.equals("ชั่วโมง")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("วัน")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("สัปดาห์")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        }

        // Create a new notification
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                mAlarmReceiverBefore.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
                mAlarmReceiver.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
                mAlarmReceiverBefore30.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                mAlarmReceiverBefore.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
                mAlarmReceiver.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
                mAlarmReceiverBefore30.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
            }
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
                if(TextUtils.isEmpty(mTitleText.getText())) {
                    mTitleText.setError("กรุณากรอกชื่อยา");
                }else {
                    updateReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("ลบแจ้งเตือน");
                builder.setMessage("ต้องการลบแจ้งเตือนใช่หรือไม่?");
                builder.setPositiveButton("ใช่", (dialog, which) -> {
                    // Delete reminder
                    alarmDatabase.deleteAlarm(mReceivedAlarm);
                    // Delete reminder alarm
                    mAlarmReceiverBefore30.cancelAlarm(getApplicationContext(), mReceivedID);
                    mAlarmReceiverBefore.cancelAlarm(getApplicationContext(), mReceivedID);
                    mAlarmReceiver.cancelAlarm(getApplicationContext(), mReceivedID);
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
}
