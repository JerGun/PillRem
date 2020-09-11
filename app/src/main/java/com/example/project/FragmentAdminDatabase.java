package com.example.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class FragmentAdminDatabase extends Fragment {

    TextView userDatabase;
    List<Admin> list;
    ListView listView;
    ArrayAdapter arrayAdapter;
    DatabaseHelper databaseHelper;
    FloatingActionButton floatingActionButton;
    Dialog dialog;
    Button button;
    EditText input_username, input_password;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_admin, container, false);

        setHasOptionsMenu(true);

        databaseHelper = new DatabaseHelper(getContext());
        userDatabase = (TextView) v.findViewById(R.id.userDatabase);
        listView = (ListView) v.findViewById(R.id.listView);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            list = databaseHelper.adminViewAllData();
            arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
            listView.setAdapter(arrayAdapter);

            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("ลบข้อมูลผู้ใช้");
            builder.setMessage("ต้องการลบข้อมูลผู้ใช้ใช่หรือไม่?");
            builder.setPositiveButton("ใช่", (dialog, which) -> {

                Admin admin = (Admin) list.get(position);
                String user_username = admin.getUsername();
                if(databaseHelper.adminDeleteData(user_username) > 0) {
                    Toast.makeText(getContext(), "ลบข้อมูลผู้ใช้เสร็จสิ้น", Toast.LENGTH_SHORT).show();
                    list.remove(admin);
                    arrayAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), "ลบข้อมูลผู้ใช้ผิดพลาด", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("ไม่", null);
            builder.show();
            return true;
        });

        userDatabase.setOnClickListener(v1 -> {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ฐานข้อมูลผู้ใช้");
            getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentById(R.id.admin_container))
                    .commit();
        });

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.admin_register);

        input_username = (EditText) dialog.findViewById(R.id.input_username);
        input_password = (EditText) dialog.findViewById(R.id.input_password);

        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(v12 -> {
            dialog.show();
        });

        button = dialog.findViewById(R.id.button);
        button.setOnClickListener(v1 -> {
            if(check_empty()) {
                if(AddNew()){
                    input_username.setText(null);
                    input_password.setText(null);
                    dialog.cancel();
                    Toast.makeText(getContext(), "ลงทะเบียนเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                }else {
                    input_username.setError("ไม่สามารถใช้ชื่อดูแลนี้");
                    Toast.makeText(getContext(), "ไม่สามารถใช้ชื่อผู้ดูแลนี้", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(getContext(), "ลงทะเบียนผิดพลาด" + "\n" + "กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        list = databaseHelper.adminViewAllData();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setQueryHint("ค้นหา");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        arrayAdapter.getFilter().filter(newText);
                        return true;
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean check_empty() {
        if(TextUtils.isEmpty(input_username.getText())){
            input_username.setError("กรุณากรอกชื่อผู้ดูแล");
            return false;
        }
        if(TextUtils.isEmpty(input_password.getText())){
            input_password.setError("กรุณากรอกรหัสผ่าน");
            return false;
        }
        return true;
    }

    public boolean AddNew(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        String username = input_username.getText().toString().toLowerCase();
        String password = input_password.getText().toString();

        Admin admin = new Admin(username, password);

        if(databaseHelper.checkAdminID(username) == 0 && databaseHelper.checkUserID(username) == 0){
            long result = databaseHelper.adminAddData(admin);
            return result != 2;
        }
        return false;
    }
}
