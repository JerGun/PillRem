package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    FloatingActionButton mAddReminderButton;
    RecyclerView mList;
    SimpleAdapter mAdapter;
    TextView mNoReminderView;
    int mTempPost;
    LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    AppointmentDatabase appointmentDatabase;
    AppointmentReceiver appointmentReceiver;
    AppointmentReceiverBefore appointmentReceiverBefore;
    MultiSelector mMultiSelector = new MultiSelector();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment);

        getSupportActionBar().setTitle("แจ้งเตือนการนัดหมาย");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize reminder database
        appointmentDatabase = new AppointmentDatabase(getApplicationContext());

        // Initialize views
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.fab);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Appointment> mTest = appointmentDatabase.getAllAppointments();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        // On clicking the floating action button
        mAddReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AppointmentAddActivity.class);
            startActivity(intent);
        });

        // Initialize alarm
        appointmentReceiver = new AppointmentReceiver();
        appointmentReceiverBefore = new AppointmentReceiverBefore();

    }

    private androidx.appcompat.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.edit_alarm_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentActivity.this);
                    builder.setTitle("ลบนัดหมาย");
                    builder.setMessage("ต้องการลบนัดหมายใช่หรือไม่?");
                    builder.setPositiveButton("ใช่", (dialog, which) -> {
                        // Close the context menu
                        actionMode.finish();

                        // Get the reminder id associated with the recycler view item
                        for (int i = IDmap.size(); i >= 0; i--) {
                            if (mMultiSelector.isSelected(i, 0)) {
                                int id = IDmap.get(i);
                                // Get reminder from reminder database using id
                                Appointment temp = appointmentDatabase.getAppointment(id);
                                // Delete reminder
                                appointmentDatabase.deleteAppointment(temp);
                                // Remove reminder from recycler view
                                mAdapter.removeItemSelected(i);
                                // Delete reminder alarm
                                appointmentReceiver.cancelAlarm(getApplicationContext(), id);
                                appointmentReceiverBefore.cancelAlarm(getApplicationContext(), id);
                            }
                        }
                        // Clear selected items in recycler view
                        mMultiSelector.clearSelections();
                        // Recreate the recycler items
                        // This is done to remap the item and reminder ids
                        mAdapter.onDeleteItem(getDefaultItemCount());
                        // Display toast to confirm delete
                        Toast.makeText(getApplicationContext(), "ลบเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                        // To check is there are saved reminders
                        // If there are no reminders display a message asking the user to create reminders
                        List<Appointment> mTest = appointmentDatabase.getAllAppointments();
                        if (mTest.isEmpty()) {
                            mNoReminderView.setVisibility(View.VISIBLE);
                        } else {
                            mNoReminderView.setVisibility(View.GONE);
                        }
                    });
                    builder.setNegativeButton("ไม่", null);
                    builder.show();
                    return true;

                case R.id.action_save:
                    // Close the context menu
                    actionMode.finish();
                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, AppointmentEditActivity.class);
        i.putExtra(AppointmentEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItemCount(getDefaultItemCount());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Appointment> mTest = appointmentDatabase.getAllAppointments();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }
        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    protected int getDefaultItemCount() {
        return 100;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<AppointmentItem> mItems;

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        // View holder for recycler view items
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.appointment_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            AppointmentItem item = mItems.get(position);
            itemHolder.setAppointmentTitle(item.mTitle);
            itemHolder.setAppointmentDoctor(item.mDoctor);
            itemHolder.setAppointmentLocation(item.mLocation);
            itemHolder.setAppointmentDateTime(item.mDateTime);
            itemHolder.setActiveImage(item.mActive);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Class for recycler view items
        public  class AppointmentItem {
            public String mTitle;
            public String mDoctor;
            public String mLocation;
            public String mDateTime;
            public String mActive;

            public AppointmentItem(String Title, String Doctor,String Location, String DateTime, String Active) {
                this.mTitle = Title;
                this.mDoctor = Doctor;
                this.mLocation = Location;
                this.mDateTime = DateTime;
                this.mActive = Active;
            }
        }

        // Class to compare date and time so that items are sorted in ascending order
        public class DateTimeComparator implements Comparator {
            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter)a).getDateTime();
                String o2 = ((DateTimeSorter)b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        // UI and data class for recycler view items
        public  class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDoctorText, mLocationText, mDateAndTimeText;
            private ImageView mActiveImage;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDoctorText = (TextView) itemView.findViewById(R.id.recycle_doctor);
                mLocationText = (TextView) itemView.findViewById(R.id.recycle_location);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
            }

            // On clicking a reminder item
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            // On long press enter action mode with context menu
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = AppointmentActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }

            // Set reminder title view
            public void setAppointmentTitle(String title) {
                mTitleText.setText("รายละเอียดการนัด: " + title);
            }

            public void setAppointmentDoctor(String doctor) {
                mDoctorText.setText("ชื่อแพทย์: " + doctor);
            }

            public void setAppointmentLocation(String location) {
                mLocationText.setText("สถานที่: " + location);
            }

            // Set date and time views
            public void setAppointmentDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // Set active image as on or off
            public void setActiveImage(String active){
                if(active.equals("true")){
                    mActiveImage.setImageResource(R.mipmap.ic_notification_on_32px);
                }else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.mipmap.ic_notification_off_32px);
                }
            }
        }

        // Generate random test data
        public  AppointmentItem generateDummyData() {
            return new AppointmentItem("1", "2", "3", "4", "5");
        }

        // Generate real data for each item
        public List<AppointmentItem> generateData(int count) {
            ArrayList<SimpleAdapter.AppointmentItem> items = new ArrayList<>();

            // Get all reminders from the database
            List<Appointment> appointments = appointmentDatabase.getAllAppointments();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Doctors = new ArrayList<>();
            List<String> Locations = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Appointment r : appointments) {
                Titles.add(r.getTitle());
                Doctors.add(r.getDoctor());
                Locations.add(r.getLocation());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;

            // Add date and time as DateTimeSorter objects
            for(int k = 0; k<Titles.size(); k++){
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                key++;
            }

            // Sort items according to date and time in ascending order
            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            // Add data to each recycler view item
            for (DateTimeSorter item:DateTimeSortList) {
                int i = item.getIndex();

                items.add(new SimpleAdapter.AppointmentItem(Titles.get(i), Doctors.get(i), Locations.get(i), DateAndTime.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
            return items;
        }
    }

}
