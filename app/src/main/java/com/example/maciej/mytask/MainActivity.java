/*
 * Copyright (c) 2015 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.maciej.mytask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private final int ADD_TASK_REQUEST = 1;
    private final int MODIFY_TASK_REQUEST = 2;
    private final String PREFS_TASKS = "prefs_tasks";
    private final String KEY_NAME_LIST = "name";
    private final String KEY_DESCRIPTION_LIST = "description";
    private final String KEY_DATE_LIST = "date";
    public static final String EXTRA_TASK_NAME = "task_name";
    public static final String EXTRA_TASK_DESCRIPTION = "task_desc";
    public static final String EXTRA_TASK_DATE = "task_date";

    private ArrayList<String> mNameList;
    private ArrayList<String> mDescriptionList;
    private ArrayList<String> mDateList;
    private ArrayAdapter<String> mAdapter;
    private TextView mDateTimeTextView;
    private BroadcastReceiver mTickReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        createNotification();

        mDateTimeTextView = (TextView) findViewById(R.id.dateTimeTextView);
//      final Button addTaskBtn = (Button) findViewById(R.id.addTaskBtn);
        final ListView listview = (ListView) findViewById(R.id.taskListview);
//      final CheckedTextView listitem = (CheckedTextView) findViewById(R.id.checkedTextView1);
        mNameList = new ArrayList<String>();
        mDescriptionList = new ArrayList<String>();
        mDateList = new ArrayList<String>();

        String savedNameList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_NAME_LIST, null);
        if (savedNameList != null) {
            String[] name_items = savedNameList.split(",");
            mNameList = new ArrayList<String>(Arrays.asList(name_items));
        }

        String savedDescriptionList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_DESCRIPTION_LIST, null);
        if (savedDescriptionList != null) {
            String[] description_items = savedDescriptionList.split(",");
            mDescriptionList = new ArrayList<String>(Arrays.asList(description_items));
        }

        String savedDateList = getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).getString(KEY_DATE_LIST, null);
        if (savedDateList != null) {
            String[] date_items = savedDateList.split(",");
            mDateList = new ArrayList<String>(Arrays.asList(date_items));
        }

        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, mNameList);
        listview.setAdapter(mAdapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView item = (CheckedTextView) view;
                if (item.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.checked), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.unchecked), Toast.LENGTH_SHORT).show();
                }
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                descriptionClicked(view);
                return true;
            }
        });

        mTickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    mDateTimeTextView.setText(getCurrentTimeStamp());
                }
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add:
                addTaskClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDateTimeTextView.setText(getCurrentTimeStamp());
        registerReceiver(mTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTickReceiver != null) {
            try {
                unregisterReceiver(mTickReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Timetick Receiver not registered", e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        StringBuilder savedNameList = new StringBuilder();
        for (String s : mNameList) {
            savedNameList.append(s);
            savedNameList.append(",");
        }
        getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit()
                .putString(KEY_NAME_LIST, savedNameList.toString()).commit();

        StringBuilder savedDescriptionList = new StringBuilder();
        for (String s : mDescriptionList) {
            savedDescriptionList.append(s);
            savedDescriptionList.append(",");
        }
        getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit()
                .putString(KEY_DESCRIPTION_LIST, savedDescriptionList.toString()).commit();

        StringBuilder savedDateList = new StringBuilder();
        for (String s : mDateList) {
            savedDateList.append(s);
            savedDateList.append(",");
        }
        getSharedPreferences(PREFS_TASKS, MODE_PRIVATE).edit()
                .putString(KEY_DATE_LIST, savedDateList.toString()).commit();
    }

    public void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.add)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_desc));
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(12, mBuilder.build());
    }

    public void addTaskClicked() {
        Intent intent = new Intent(MainActivity.this, NewTaskActivity.class);
        startActivityForResult(intent, ADD_TASK_REQUEST);
    }

    public void descriptionClicked(View view) {
        CheckedTextView item = (CheckedTextView) view;
        String item_name = item.getText().toString();
        int index = mNameList.indexOf(item_name);
        String item_description = mDescriptionList.get(index);
        String item_date = mDateList.get(index);
        Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
        Bundle extras = new Bundle();
        extras.putString(EXTRA_TASK_NAME, item_name);
        extras.putString(EXTRA_TASK_DESCRIPTION, item_description);
        extras.putString(EXTRA_TASK_DATE, item_date);
        intent.putExtras(extras);
        startActivityForResult(intent, MODIFY_TASK_REQUEST);
    }

    public void deleteItem(int index) {
        mNameList.remove(index);
        mDescriptionList.remove(index);
        mDateList.remove(index);
    }

    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdf.format(now);
        return strDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                String taskName = data.getStringExtra(NewTaskActivity.EXTRA_TASK_NAME);
                String taskDescription = data.getStringExtra(NewTaskActivity.EXTRA_TASK_DESCRIPTION);
                String taskDate = data.getStringExtra(NewTaskActivity.EXTRA_TASK_DATE);
                mNameList.add(taskName);
                mDescriptionList.add(taskDescription);
                mDateList.add(taskDate);
                mAdapter.notifyDataSetChanged();
            }
        }
        if (requestCode == MODIFY_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                String taskName = data.getStringExtra(DescriptionActivity.EXTRA_TASK_NAME);
                String taskDescription = data.getStringExtra(DescriptionActivity.EXTRA_TASK_DESCRIPTION);
                String taskDate = data.getStringExtra(DescriptionActivity.EXTRA_TASK_DATE);
                String taskOldName = data.getStringExtra(DescriptionActivity.EXTRA_TASK_OLDNAME);
                Boolean taskDelete = data.getBooleanExtra(DescriptionActivity.EXTRA_TASK_DELETE, false);
                int index = mNameList.indexOf(taskOldName);
                if (taskDelete == false) {
                    mNameList.set(index, taskName);
                    mDescriptionList.set(index, taskDescription);
                    mDateList.set(index, taskDate);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    deleteItem(index);
                }
            }
        }
    }

}
