package com.example.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ProfileActivity extends AppCompatActivity {

    String name, address, phone, blood, allergic, treatment, emergency_name, emergency_phone, getUsername;
    TextView text_name, text_address, text_phone,text_blood, text_allergic, text_treatment, text_emergency_name, text_emergency_phone;
    DatabaseHelper databaseHelper;
    MenuItem menuItem;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Intent getIntent = getIntent();
        getUsername = getIntent.getStringExtra("username");

        getSupportActionBar().setTitle("โปรไฟล์");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        text_name = (TextView) findViewById(R.id.text_name);
        text_address = (TextView) findViewById(R.id.text_address);
        text_phone = (TextView) findViewById(R.id.text_phone);
        text_blood = (TextView) findViewById(R.id.text_blood);
        text_allergic = (TextView) findViewById(R.id.text_allergic);
        text_treatment = (TextView) findViewById(R.id.text_treatment);
        text_emergency_name = (TextView) findViewById(R.id.text_emergency_name);
        text_emergency_phone = (TextView) findViewById(R.id.text_emergency_phone);

        Cursor cursor = databaseHelper.userViewData(getUsername);
        cursor.moveToPosition(0);

        name = cursor.getString(3) + " " + cursor.getString(4);
        address = cursor.getString(5);
        phone = cursor.getString(6);
        blood = cursor.getString(7);
        allergic = cursor.getString(8);
        treatment = cursor.getString(9);
        emergency_name = cursor.getString(10) + " " + cursor.getString(11);
        emergency_phone = cursor.getString(12);

        setShow();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Cursor cursor1 = databaseHelper.userViewData(getUsername);
            cursor1.moveToPosition(0);

            name = cursor1.getString(3) + " " + cursor1.getString(4);
            address = cursor1.getString(5);
            phone = cursor1.getString(6);
            blood = cursor1.getString(7);
            allergic = cursor1.getString(8);
            treatment = cursor1.getString(9);
            emergency_name = cursor1.getString(10) + " " + cursor1.getString(11);
            emergency_phone = cursor1.getString(12);

            setShow();

            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        menuItem = menu.findItem(R.id.action_edit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit:
                Intent intent = new Intent(this, EditProfileActivity.class);
                intent.putExtra("username", getUsername);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setShow() {
        text_name.setText(name);
        text_address.setText(address);
        text_phone.setText(phone);
        text_blood.setText(blood);
        text_emergency_name.setText(emergency_name);
        text_emergency_phone.setText(emergency_phone);
        text_allergic.setText(allergic);
        text_treatment.setText(treatment);
    }
}