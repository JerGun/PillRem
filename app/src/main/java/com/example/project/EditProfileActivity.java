package com.example.project;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    String getUsername, getName, getSurname, getAddress, getPhone, getAllergic, getTreatment, getEmergency_name,getEmergency_surname, getEmergency_phone
            ,name, surname, address, phone, blood, allergic, treatment, emergency_name, emergency_surname, emergency_phone;
    EditText edit_name, edit_surname, edit_address, edit_phone, edit_allergic, edit_treatment, edit_emergency_name, edit_emergency_surname, edit_emergency_phone;
    Spinner spinner;
    Button button_save, allergic_add, treatment_add;
    RadioButton radioButtonNotAllergic, radioButtonAllergic, radioButtonNotTreatment, radioButtonTreatment;
    DatabaseHelper databaseHelper;
    Dialog allergic_dialog, treatment_dialog;
    ListView allergic_listView, treatment_listView;
    TextView allergic_show, treatment_show;
    FrameLayout allergic_close, treatment_close;
    List<String> allergicArrayList, treatmentArrayList;
    ArrayAdapter<String> allergicArrayAdapter, treatmentArrayAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        Intent getIntent = getIntent();
        getUsername = getIntent.getStringExtra("username");

        getSupportActionBar().setTitle("แก้ไขโปรไฟล์");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_surname = (EditText) findViewById(R.id.edit_surname);
        edit_address = (EditText) findViewById(R.id.edit_address);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_emergency_name = (EditText) findViewById(R.id.edit_emergency_name);
        edit_emergency_surname = (EditText) findViewById(R.id.edit_emergency_last_name);
        edit_emergency_phone = (EditText) findViewById(R.id.edit_emergency_phone);
        radioButtonNotAllergic = (RadioButton) findViewById(R.id.not_allergic);
        radioButtonAllergic = (RadioButton) findViewById(R.id.allergic);
        radioButtonNotTreatment = (RadioButton) findViewById(R.id.not_treatment);
        radioButtonTreatment = (RadioButton) findViewById(R.id.treatment);
        allergic_show = (TextView) findViewById(R.id.allergic_show);
        treatment_show = (TextView) findViewById(R.id.treatment_show);

        databaseHelper = new DatabaseHelper(this);
        Cursor cursor = databaseHelper.userViewData(getUsername);
        cursor.moveToPosition(0);

        getName = cursor.getString(3);
        getSurname = cursor.getString(4);
        getAddress = cursor.getString(5);
        getPhone = cursor.getString(6);
        getAllergic = cursor.getString(8);
        getTreatment = cursor.getString(9);
        getEmergency_name = cursor.getString(10);
        getEmergency_surname = cursor.getString(11);
        getEmergency_phone = cursor.getString(12);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button_save = (Button) findViewById(R.id.button_save);
        button_save.setOnClickListener(v1 -> {
            if(checkEmpty()) {
                if(updateData()) {
                    finish();
                    Toast.makeText(this, "บันทึกเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "บันทึกผิดพลาด", Toast.LENGTH_SHORT).show();
                }
            }
        });

        radioButtonNotAllergic.setOnClickListener(v -> {
            allergic_show.setVisibility(View.GONE);
            allergic = "ไม่แพ้ยา";
        });

        radioButtonAllergic.setOnClickListener(v -> allergic_show.setVisibility(View.VISIBLE));

        radioButtonNotTreatment.setOnClickListener(v -> {
            treatment_show.setVisibility(View.GONE);
            treatment = "ไม่เคยรับการรักษา";
        });

        radioButtonTreatment.setOnClickListener(v -> treatment_show.setVisibility(View.VISIBLE));

        //allergic
        allergic_dialog = new Dialog(this);
        allergic_dialog.setContentView(R.layout.allergic_popup);

        edit_allergic = (EditText) allergic_dialog.findViewById(R.id.input_allergic);
        allergic_add = (Button) allergic_dialog.findViewById(R.id.add_more);
        allergic_listView = (ListView) allergic_dialog.findViewById(R.id.listView);

        allergic_show.setOnClickListener(v -> {
            if(getAllergic.equals("ไม่แพ้ยา")){
                allergicArrayList = new ArrayList<> ();
            }else{
                allergicArrayList = new ArrayList<> (Arrays.asList(getAllergic.split(",\\s*")));
            }
            allergicArrayAdapter = new ArrayAdapter<> (this, android.R.layout.simple_list_item_1, allergicArrayList);
            allergic_listView.setAdapter(allergicArrayAdapter);

            allergic_dialog.show();
            Window window = allergic_dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            allergic_add.setOnClickListener(v1 -> {
                if(TextUtils.isEmpty(edit_allergic.getText())) {
                    edit_allergic.setError("กรุณากรอกชื่อยา");
                }else{
                    allergicArrayList.add(edit_allergic.getText().toString());
                    edit_allergic.getText().clear();
                    allergicArrayAdapter.notifyDataSetChanged();
                }
            });

            allergic_listView.setOnItemClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("ลบยา");
                builder.setMessage("ต้องการลบยาชนิดนี้ใช่หรือไม่?");
                builder.setPositiveButton("ใช่", (dialog, which) -> {
                    allergicArrayList.remove(position);
                    allergicArrayAdapter.notifyDataSetChanged();
                });
                builder.setNegativeButton("ไม่", null);
                builder.show();
            });

            allergic_close = (FrameLayout) allergic_dialog.findViewById(R.id.close);
            allergic_close.setOnClickListener(v2 -> allergic_dialog.cancel());
        });

        allergic_dialog.setOnDismissListener(dialog -> {
            if(allergicArrayToString().length() == 0){
                allergic_show.setText("กดเพื่อเพิ่มยาที่แพ้");
                allergic_show.setError("กรุณาเพิ่มยาที่แพ้");
            }else {
                allergic_show.setText(allergicArrayToString());
                allergic_show.setError(null);
            }
        });

        //treatment
        treatment_dialog = new Dialog(this);
        treatment_dialog.setContentView(R.layout.treatment_popup);

        edit_treatment = (EditText) treatment_dialog.findViewById(R.id.input_treatment);
        treatment_add = (Button) treatment_dialog.findViewById(R.id.add_more);
        treatment_listView = (ListView) treatment_dialog.findViewById(R.id.listView);

        treatment_show.setOnClickListener(v -> {
            if(getTreatment.equals("ไม่เคยรับการรักษา")){
                treatmentArrayList = new ArrayList<> ();
            }else{
                treatmentArrayList = new ArrayList<>(Arrays.asList(getTreatment.split(",\\s*")));
            }
            treatmentArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, treatmentArrayList);
            treatment_listView.setAdapter(treatmentArrayAdapter);

            treatment_dialog.show();
            Window window = treatment_dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            treatment_add.setOnClickListener(v1 -> {
                if(TextUtils.isEmpty(edit_treatment.getText())) {
                    edit_treatment.setError("กรุณากรอกชื่อยา");
                }else{
                    treatmentArrayList.add(edit_treatment.getText().toString());
                    edit_treatment.getText().clear();
                    treatmentArrayAdapter.notifyDataSetChanged();
                }
            });

            treatment_listView.setOnItemClickListener((parent, view, position, id) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("ลบยา");
                builder.setMessage("ต้องการลบยาชนิดนี้ใช่หรือไม่?");
                builder.setPositiveButton("ใช่", (dialog, which) -> {
                    treatmentArrayList.remove(position);
                    treatmentArrayAdapter.notifyDataSetChanged();
                });
                builder.setNegativeButton("ไม่", null);
                builder.show();
            });

            treatment_close = (FrameLayout) treatment_dialog.findViewById(R.id.close);
            treatment_close.setOnClickListener(v2 -> treatment_dialog.cancel());
        });

        treatment_dialog.setOnDismissListener(dialog -> {
            if(treatmentArrayToString().length() == 0){
                treatment_show.setText("กดเพื่อเพิ่มสถานที่ที่เคยรับการรักษา");
                treatment_show.setError("กรุณาเพิ่มสถานที่");
            }else {
                treatment_show.setText(treatmentArrayToString());
                treatment_show.setError(null);
            }
        });

        setShow();
    }

    public String allergicArrayToString() {
        String prefix = "";
        StringBuilder builder = new StringBuilder();
        for (String value : allergicArrayList) {
            builder.append(prefix);
            prefix = ", ";
            builder.append(value);
        }
        String text = builder.toString();
        return text;
    }

    public String treatmentArrayToString() {
        String prefix = "";
        StringBuilder builder = new StringBuilder();
        for (String value : treatmentArrayList) {
            builder.append(prefix);
            prefix = ", ";
            builder.append(value);
        }
        String text = builder.toString();
        return text;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean updateData(){
        name = edit_name.getText().toString();
        surname = edit_surname.getText().toString();
        address = edit_address.getText().toString();
        phone = edit_phone.getText().toString();
        blood = spinner.getSelectedItem().toString();

        if(radioButtonAllergic.isChecked()){
            allergic = allergicArrayToString();
        }else if(radioButtonNotAllergic.isChecked()){
            allergic = "ไม่แพ้ยา";
        }
        if(radioButtonTreatment.isChecked()){
            treatment = treatmentArrayToString();
        }else if(radioButtonNotTreatment.isChecked()){
            treatment = "ไม่เคยรับการรักษา";
        }

        emergency_name = edit_emergency_name.getText().toString();
        emergency_surname = edit_emergency_surname.getText().toString();
        emergency_phone = edit_emergency_phone.getText().toString();

        User user = new User(name, surname, address, phone, blood, allergic, treatment, emergency_name, emergency_surname, emergency_phone);

        if(databaseHelper.userUpdateData(user, getUsername) != 1) {
            return false;
        }
        return true;
    }

    public void setShow() {
        treatmentArrayList = new ArrayList<> (Arrays.asList(getTreatment.split(",\\s*")));
        allergicArrayList = new ArrayList<> (Arrays.asList(getAllergic.split(",\\s*")));

        edit_name.setText(getName);
        edit_surname.setText(getSurname);
        edit_address.setText(getAddress);
        edit_phone.setText(getPhone);

        if(getAllergic.equals("ไม่แพ้ยา")){
            radioButtonNotAllergic.setChecked(true);
        }else {
            radioButtonAllergic.setChecked(true);
            allergic_show.setVisibility(View.VISIBLE);
            allergic_show.setText(allergicArrayToString());
        }

        if(getTreatment.equals("ไม่เคยรับการรักษา")){
            radioButtonNotTreatment.setChecked(true);
        }else {
            radioButtonTreatment.setChecked(true);
            treatment_show.setVisibility(View.VISIBLE);
            treatment_show.setText(treatmentArrayToString());
        }

        edit_emergency_name.setText(getEmergency_name);
        edit_emergency_surname.setText(getEmergency_surname);
        edit_emergency_phone.setText(getEmergency_phone);
    }

    public boolean checkEmpty() {
        if(TextUtils.isEmpty(edit_name.getText())) {
            edit_name.setError("กรุณากรอกชื่อ");
            return false;
        }
        if(TextUtils.isEmpty(edit_surname.getText())) {
            edit_surname.setError("กรุณากรอกนามสกุล");
            return false;
        }
        if(TextUtils.isEmpty(edit_address.getText())) {
            edit_address.setError("กรุณากรอกที่อยู่");
            return false;
        }
        if(TextUtils.isEmpty(edit_phone.getText())) {
            edit_phone.setError("กรุณากรอกเบอร์โทร");
            return false;
        }
        if(radioButtonAllergic.isChecked()){
            if(allergicArrayToString().length() == 0){
                allergic_show.setError("กรุณาเพิ่มยาที่แพ้");
                return false;
            }
        }
        if(radioButtonTreatment.isChecked()){
            if(treatmentArrayToString().length() == 0){
                allergic_show.setError("กรุณาเพิ่มสถานที่");
                return false;
            }
        }
        if(TextUtils.isEmpty(edit_emergency_name.getText())){
            edit_emergency_name.setError("กรุณากรอกชื่อผู้ติดต่อฉุกเฉิน");
            return false;
        }
        if(TextUtils.isEmpty(edit_emergency_surname.getText())){
            edit_emergency_surname.setError("กรุณากรอกนามสกุลผู้ติดต่อฉุกเฉิน");
            return false;
        }
        if(TextUtils.isEmpty(edit_emergency_phone.getText())){
            edit_emergency_phone.setError("กรุณากรอกเบอร์โทรศัพท์ผู้ติดต่อฉุกเฉิน");
            return false;
        }
        return true;
    }
}
