package com.example.project;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    boolean toExit = false;

    TextView register;
    EditText get_username, get_password, input_emergency_name;
    Button submit;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        register = (TextView) findViewById(R.id.register);
        get_username = (EditText) findViewById(R.id.username);
        get_password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.button_login);

        getSupportActionBar().hide();

        databaseHelper = new DatabaseHelper(this);

        if (databaseHelper.checkStateCount() == 1) {
            Cursor cursor = databaseHelper.checkState();
            cursor.moveToPosition(0);
            String username = cursor.getString(0);

            finish();
            Intent intent = new Intent(MainActivity.this, IndexActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        }

        submit.setOnClickListener(v -> {
            checkEditText();
            String username = get_username.getText().toString().trim().toLowerCase();
            String password = get_password.getText().toString().trim();
            if (databaseHelper.checkUserLogin(username, password) != 0) {
                finish();
                Intent intent = new Intent(this, IndexActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                String state = "true";
                User user = new User(state);
                databaseHelper.userUpdateState(user, username);
            } else if (databaseHelper.checkAdminLogin(username, password) != 0) {
                get_username.setText(null);
                get_password.setText(null);
                Intent intent = new Intent(this, AdminActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(v -> {
            get_username.setText(null);
            get_password.setText(null);
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(toExit){
            super.onBackPressed();
            return;
        }
        this.toExit = true;
        Toast.makeText(this, "กดอีกครั้งเพื่อออก", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> toExit = false, 2000);
    }

    public void checkEditText() {
        if(TextUtils.isEmpty(get_username.getText())) {
            get_username.setError("กรุณากรอกชื่อผู้ใช้");
        }
        if(TextUtils.isEmpty(get_password.getText())) {
            get_password.setError("กรุณากรอกรหัสผ่าน");
        }
    }

}
