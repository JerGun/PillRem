package com.example.project;

import android.app.AlertDialog;
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

public class AlarmActivity extends AppCompatActivity {
    FloatingActionButton mAddReminderButton;
    RecyclerView mList;
    AlarmActivity.SimpleAdapter mAdapter;
    TextView mNoReminderView;
    int mTempPost;
    LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    AlarmDatabaseHelper rb;
    AlarmReceiverBefore mAlarmReceiverBefore;
    AlarmReceiverBefore30 mAlarmReceiverBefore30;
    AlarmReceiver mAlarmReceiver;
    MultiSelector mMultiSelector = new MultiSelector();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        getSupportActionBar().setTitle("แจ้งเตือนกินยา");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize reminder database
        rb = new AlarmDatabaseHelper(getApplicationContext());

        // Initialize views
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.fab);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Alarm> mTest = rb.getAllAlarms();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new AlarmActivity.SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        // On clicking the floating action button
        mAddReminderButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AlarmAddActivity.class);
            startActivity(intent);
        });

        // Initialize alarm
        mAlarmReceiverBefore30 = new AlarmReceiverBefore30();
        mAlarmReceiverBefore = new AlarmReceiverBefore();
        mAlarmReceiver = new AlarmReceiver();

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

                // On clicking discard reminders
                case R.id.action_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);
                    builder.setTitle("ลบแจ้งเตือน");
                    builder.setMessage("ต้องการลบแจ้งเตือนใช่หรือไม่?");
                    builder.setPositiveButton("ใช่", (dialog, which) -> {
                        // Close the context menu
                        actionMode.finish();
                        // Get the reminder id associated with the recycler view item
                        for (int i = IDmap.size(); i >= 0; i--) {
                            if (mMultiSelector.isSelected(i, 0)) {
                                int id = IDmap.get(i);

                                // Get reminder from reminder database using id
                                Alarm temp = rb.getAlarm(id);
                                // Delete reminder
                                rb.deleteAlarm(temp);
                                // Remove reminder from recycler view
                                mAdapter.removeItemSelected(i);
                                // Delete reminder alarm
                                mAlarmReceiverBefore30.cancelAlarm(getApplicationContext(), id);
                                mAlarmReceiverBefore.cancelAlarm(getApplicationContext(), id);
                                mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
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
                        List<Alarm> mTest = rb.getAllAlarms();
                        if (mTest.isEmpty()) {
                            mNoReminderView.setVisibility(View.VISIBLE);
                        } else {
                            mNoReminderView.setVisibility(View.GONE);
                        }
                        Toast.makeText(getApplicationContext(), "ลบเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                    });
                    builder.setNegativeButton("ไม่", null);
                    builder.show();
                    return true;

                // On clicking save reminders
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

    private void selectAlarm(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, AlarmEditActivity.class);
        i.putExtra(AlarmEditActivity.EXTRA_REMINDER_ID, mStringClickID);
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
        List<Alarm> mTest = rb.getAllAlarms();

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

    public class SimpleAdapter extends RecyclerView.Adapter<AlarmActivity.SimpleAdapter.VerticalItemHolder> {
        private ArrayList<AlarmActivity.SimpleAdapter.AlarmItem> mItems;

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
        public AlarmActivity.SimpleAdapter.VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.alarm_items, container, false);

            return new AlarmActivity.SimpleAdapter.VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(AlarmActivity.SimpleAdapter.VerticalItemHolder itemHolder, int position) {
            AlarmActivity.SimpleAdapter.AlarmItem item = mItems.get(position);
            itemHolder.setAlarmTitle(item.mTitle);
            itemHolder.setAlarmDateTime(item.mDateTime);
            itemHolder.setAlarmRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setActiveImage(item.mActive);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Class for recycler view items
        public  class AlarmItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;

            public AlarmItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
                this.mTitle = Title;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
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
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mActiveImage;
            private AlarmActivity.SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
            }

            // On clicking a reminder item
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectAlarm(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            // On long press enter action mode with context menu
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = AlarmActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }

            // Set reminder title view
            public void setAlarmTitle(String title) {
                mTitleText.setText("ชื่อยา: " + title);
            }

            // Set date and time views
            public void setAlarmDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // Set repeat views
            public void setAlarmRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if(repeat.equals("true")){
                    mRepeatInfoText.setText("ทุกๆ " + repeatNo + " " + repeatType);
                }else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("ไม่ตั้งซ้ำ");
                }
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
        public AlarmActivity.SimpleAdapter.AlarmItem generateDummyData() {
            return new AlarmActivity.SimpleAdapter.AlarmItem("1", "2", "3", "4", "5", "6");
        }

        // Generate real data for each item
        public List<AlarmActivity.SimpleAdapter.AlarmItem> generateData(int count) {
            ArrayList<AlarmActivity.SimpleAdapter.AlarmItem> items = new ArrayList<>();

            // Get all reminders from the database
            List<Alarm> alarm = rb.getAllAlarms();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Alarm r : alarm) {
                Titles.add(r.getTitle());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
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
            Collections.sort(DateTimeSortList, new AlarmActivity.SimpleAdapter.DateTimeComparator());

            int k = 0;

            // Add data to each recycler view item
            for (DateTimeSorter item:DateTimeSortList) {
                int i = item.getIndex();

                items.add(new AlarmActivity.SimpleAdapter.AlarmItem(Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
            return items;
        }
    }
}
