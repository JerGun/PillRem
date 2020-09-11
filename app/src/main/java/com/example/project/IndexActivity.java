package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class IndexActivity extends AppCompatActivity {

    String username;
    ImageButton profile, alarm, appointment;
    DatabaseHelper databaseHelper;

    boolean toExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent getIntent = getIntent();
        username = getIntent.getStringExtra("username");

        databaseHelper = new DatabaseHelper(this);

        profile = (ImageButton) findViewById(R.id.profile);
        alarm = (ImageButton) findViewById(R.id.alarm);
        appointment = (ImageButton) findViewById(R.id.appointment);

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        alarm.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        });

        appointment.setOnClickListener(v -> {
            Intent intent = new Intent(this, AppointmentActivity.class);
            startActivity(intent);
        });

        Button button = (Button) findViewById(R.id.button_logout);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            String state = "false";
            User user = new User(state);
            databaseHelper.userUpdateState(user, username);
        });
    }

    @Override
    public void onBackPressed() {
        if (toExit) {
            finish();
            return;
        }
        this.toExit = true;
        Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> toExit = false, 2000);
    }
}