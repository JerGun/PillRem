package com.example.project;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);

        getSupportActionBar().setTitle("ฐานข้อมูลผู้ใช้");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportFragmentManager().findFragmentById(R.id.admin_container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.admin_container, new FragmentUserDatabase())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.admin_container);
        if(fragment instanceof FragmentAdminDatabase) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.admin_container);
                if(fragment instanceof FragmentAdminDatabase) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                    getSupportActionBar().setTitle("ฐานข้อมูลผู้ใช้");
                }else {
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
