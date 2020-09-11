package com.example.project;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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

import java.util.List;

public class FragmentUserDatabase extends Fragment {

    TextView adminDatabase, user_name_txt, user_address_txt, user_phone_txt, user_blood_txt, user_allergic_txt,
            user_treatment_txt, user_emergencyName_txt, user_emergencyPhone_txt;
    FrameLayout close;
    Dialog dialog;
    List<User> list;
    ListView listView;
    ArrayAdapter arrayAdapter;
    DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_database_user, container, false);

        setHasOptionsMenu(true);

        databaseHelper = new DatabaseHelper(getContext());
        adminDatabase = (TextView) v.findViewById(R.id.adminDatabase);
        listView = (ListView) v.findViewById(R.id.listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) list.get(position);
            String user_name = user.getName() + " " + user.getSurname();
            String user_address = user.getAddress();
            String user_phone = user.getPhone();
            String user_blood = user.getBlood();
            String user_allergic = user.getAllergic();
            String user_treatment = user.getTreatment();
            String user_emergency_name = user.getEmergency_name() + " " + user.getEmergency_surname();
            String user_emergency_phone = user.getEmergency_phone();

            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.user_popup);
            dialog.show();
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            user_name_txt = dialog.findViewById(R.id.user_name);
            user_name_txt.setText(user_name);
            user_address_txt = dialog.findViewById(R.id.user_address);
            user_address_txt.setText(user_address);
            user_phone_txt = dialog.findViewById(R.id.user_phone);
            user_phone_txt.setText(user_phone);
            user_blood_txt = dialog.findViewById(R.id.user_blood);
            user_blood_txt.setText(user_blood);
            user_allergic_txt = dialog.findViewById(R.id.user_allergic);
            user_allergic_txt.setText(user_allergic);
            user_treatment_txt = dialog.findViewById(R.id.user_treatment);
            user_treatment_txt.setText(user_treatment);
            user_emergencyName_txt = dialog.findViewById(R.id.user_emergency_name);
            user_emergencyName_txt.setText(user_emergency_name);
            user_emergencyPhone_txt = dialog.findViewById(R.id.user_emergency_phone);
            user_emergencyPhone_txt.setText(user_emergency_phone);

            close = (FrameLayout) dialog.findViewById(R.id.close);
            close.setOnClickListener(v12 -> dialog.cancel());
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("ลบข้อมูลผู้ใช้");
            builder.setMessage("ต้องการลบข้อมูลผู้ใช้ใช่หรือไม่?");
            builder.setPositiveButton("ใช่", (dialog, which) -> {
                User user = (User) list.get(position);
                String user_username = user.getUsername();

                if(databaseHelper.userDeleteData(user_username) > 0) {
                    Toast.makeText(getContext(), "ลบข้อมูลผู้ใช้เสร็จสิ้น", Toast.LENGTH_SHORT).show();
                    list.remove(user);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "ลบข้อมูลผู้ใช้ผิดพลาด", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("ไม่", null);
            builder.show();
            return true;
        });

        adminDatabase.setOnClickListener(v1 -> {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ฐานข้อมูลผู้ดูแล");
            Fragment fragment = new FragmentAdminDatabase();
            getFragmentManager().beginTransaction()
                    .add(R.id.admin_container, fragment)
                    .commit();
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        list = databaseHelper.userViewAllData();
        arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
}
