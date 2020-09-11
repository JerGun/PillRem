package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RegisterActivity extends AppCompatActivity {

    EditText input_username, input_password, input_name, input_surname, input_allergic, input_treatment
            , input_address, input_phone, input_emergency_name, input_emergency_surname, input_emergency_phone;
    Spinner spinner;
    Button button, allergic_add, treatment_add;
    RadioButton radioButtonNotAllergic, radioButtonAllergic, radioButtonNotTreatment, radioButtonTreatment;
    String username, password, name, surname, address, phone, blood, allergic, treatment, emergency_name, emergency_surname, emergency_phone, state;
    Dialog allergic_dialog, treatment_dialog;
    ListView allergic_listView, treatment_listView;
    TextView allergic_show, treatment_show;
    FrameLayout allergic_close, treatment_close;
    String[] allergicList, treatmentList;
    List<String> allergicArrayList, treatmentArrayList;
    ArrayAdapter<String> allergicArrayAdapter, treatmentArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        getSupportActionBar().hide();

        input_username = (EditText) findViewById(R.id.input_username);
        input_password = (EditText) findViewById(R.id.input_password);
        input_name = (EditText) findViewById(R.id.input_name);
        input_surname = (EditText) findViewById(R.id.input_last_name);
        input_address = (EditText) findViewById(R.id.input_address);
        input_phone = (EditText) findViewById(R.id.input_phone);
        input_emergency_name = (EditText) findViewById(R.id.input_emergency_name);
        input_emergency_surname = (EditText) findViewById(R.id.input_emergency_surname);
        input_emergency_phone = (EditText) findViewById(R.id.input_emergency_phone);
        radioButtonNotAllergic = (RadioButton) findViewById(R.id.not_allergic);
        radioButtonAllergic = (RadioButton) findViewById(R.id.allergic);
        radioButtonNotTreatment = (RadioButton) findViewById(R.id.not_treatment);
        radioButtonTreatment = (RadioButton) findViewById(R.id.treatment);
        allergic_show = (TextView) findViewById(R.id.allergic_show);
        treatment_show = (TextView) findViewById(R.id.treatment_show);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.blood, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        button = (Button) findViewById(R.id.button_register);
        button.setOnClickListener(v1 -> {
            if (check_empty()) {
                if (addNew()) {
                    Toast.makeText(this, "ลงทะเบียนเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }else {
                    input_username.setError("ไม่สามารถใช้ชื่อผู้ใช้นี้");
                    Toast.makeText(this, "ไม่สามารถใช้ชื่อผู้ใช้นี้", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "ลงทะเบียนผิดพลาด" + "\n" + "กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonNotAllergic.setOnClickListener(v12 -> {
            allergic_show.setVisibility(View.GONE);
            allergic = "ไม่แพ้ยา";
            radioButtonNotAllergic.setTextColor(getColor(R.color.black));
            radioButtonAllergic.setTextColor(getColor(R.color.grey2));
        });

        radioButtonAllergic.setOnClickListener(v13 -> {
            allergic_show.setVisibility(View.VISIBLE);
            radioButtonAllergic.setTextColor(getColor(R.color.black));
            radioButtonNotAllergic.setTextColor(getColor(R.color.grey2));
        });

        radioButtonNotTreatment.setOnClickListener(v14 -> {
            treatment_show.setVisibility(View.GONE);
            treatment = "ไม่เคยรับการรักษา";
            radioButtonNotTreatment.setTextColor(getColor(R.color.black));
            radioButtonTreatment.setTextColor(getColor(R.color.grey2));
        });

        radioButtonTreatment.setOnClickListener(v15 -> {
            treatment_show.setVisibility(View.VISIBLE);
            radioButtonTreatment.setTextColor(getColor(R.color.black));
            radioButtonNotTreatment.setTextColor(getColor(R.color.grey2));
        });

        //allergic
        allergic_dialog = new Dialog(this);
        allergic_dialog.setContentView(R.layout.allergic_popup);

        //allergic attributes
        input_allergic = (EditText) allergic_dialog.findViewById(R.id.input_allergic);
        allergic_add = (Button) allergic_dialog.findViewById(R.id.add_more);
        allergic_listView = (ListView) allergic_dialog.findViewById(R.id.listView);

        //allergic list
        allergicList = new String[]{};
        allergicArrayList = new ArrayList<> (Arrays.asList(allergicList));
        allergicArrayAdapter = new ArrayAdapter<> (this, android.R.layout.simple_list_item_1, allergicArrayList);
        allergic_listView.setAdapter(allergicArrayAdapter);

        //allergic show
        allergic_show.setOnClickListener(v -> {
            allergic_dialog.show();
            Window window = allergic_dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            allergic_add.setOnClickListener(v1 -> {
                if(TextUtils.isEmpty(input_allergic.getText())) {
                    input_allergic.setError("กรุณากรอกชื่อยา");
                }else{
                    allergicArrayList.add(input_allergic.getText().toString());
                    input_allergic.getText().clear();
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

        //treatment attributes
        input_treatment = (EditText) treatment_dialog.findViewById(R.id.input_treatment);
        treatment_add = (Button) treatment_dialog.findViewById(R.id.add_more);
        treatment_listView = (ListView) treatment_dialog.findViewById(R.id.listView);

        //treatment list
        treatmentList = new String[]{};
        treatmentArrayList = new ArrayList<>(Arrays.asList(treatmentList));
        treatmentArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, treatmentArrayList);
        treatment_listView.setAdapter(treatmentArrayAdapter);

        //treatment show
        treatment_show.setOnClickListener(v -> {
            treatment_dialog.show();
            Window window = treatment_dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            treatment_add.setOnClickListener(v1 -> {
                if(TextUtils.isEmpty(input_treatment.getText())) {
                    input_treatment.setError("กรุณากรอกชื่อยา");
                }else{
                    treatmentArrayList.add(input_treatment.getText().toString());
                    input_treatment.getText().clear();
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

    public boolean check_empty() {
        if(TextUtils.isEmpty(input_username.getText())){
            input_username.setError("กรุณากรอกชื่อผู้ใช้");
            return false;
        }
        if(TextUtils.isEmpty(input_password.getText())){
            input_password.setError("กรุณากรอกรหัสผ่าน");
            return false;
        }
        if(TextUtils.isEmpty(input_name.getText())) {
            input_name.setError("กรุณากรอกชื่อ");
            return false;
        }
        if(TextUtils.isEmpty(input_surname.getText())) {
            input_surname.setError("กรุณากรอกนามสกุล");
            return false;
        }
        if(TextUtils.isEmpty(input_phone.getText())){
            input_phone.setError("กรุณากรอกเบอร์โทรศัพท์");
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
        if(TextUtils.isEmpty(input_emergency_name.getText())){
            input_emergency_name.setError("กรุณากรอกชื่อผู้ติดต่อฉุกเฉิน");
            return false;
        }
        if(TextUtils.isEmpty(input_emergency_surname.getText())){
            input_emergency_surname.setError("กรุณากรอกนามสกุลผู้ติดต่อฉุกเฉิน");
            return false;
        }
        if(TextUtils.isEmpty(input_emergency_phone.getText())){
            input_emergency_phone.setError("กรุณากรอกเบอร์โทรศัพท์ผู้ติดต่อฉุกเฉิน");
            return false;
        }
        return true;
    }

    public boolean addNew(){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        username = input_username.getText().toString().toLowerCase();
        password = input_password.getText().toString();
        name = input_name.getText().toString();
        surname = input_surname.getText().toString();
        address = input_address.getText().toString();
        phone = input_phone.getText().toString();
        blood = spinner.getSelectedItem().toString();

        if(radioButtonAllergic.isChecked()){
            allergic = allergicArrayToString();
        }
        if(radioButtonTreatment.isChecked()){
            treatment = treatmentArrayToString();
        }

        emergency_name = input_emergency_name.getText().toString();
        emergency_surname = input_emergency_surname.getText().toString();
        emergency_phone = input_emergency_phone.getText().toString();
        state = "false";

        User user = new User(username, password, name, surname, address, phone, blood,
                allergic, treatment, emergency_name, emergency_surname, emergency_phone, state);

        if(databaseHelper.checkAdminID(username) == 0 && databaseHelper.checkUserID(username) == 0){
            long result = databaseHelper.userAddData(user);
            return result != 13;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backToMain(View view) {
        onBackPressed();
    }
}