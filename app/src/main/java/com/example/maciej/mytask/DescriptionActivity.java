package com.example.maciej.mytask;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DescriptionActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_NAME = "task_name";
    public static final String EXTRA_TASK_DESCRIPTION = "task_desc";
    public static final String EXTRA_TASK_DATE = "task_date";
    public static final String EXTRA_TASK_OLDNAME = "task_oldname";
    public static final String EXTRA_TASK_DELETE = "task_delete";
    public static final String NAME_EDIT = "name_edit";
    public static final String DESCRIPTION_EDIT = "description_edit";
    public String oldTaskName;

    private TextView mNameView;
    private TextView mDescriptionView;
    private TextView mDateView;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        mNameView = (TextView) findViewById(R.id.nameText);
        mDescriptionView = (TextView) findViewById(R.id.descriptionText);
        mDateView = (TextView) findViewById(R.id.dateText);

        Intent intent = getIntent();
        setDateTimeField();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        completeData(intent);

        mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextClick(NAME_EDIT);
            }
        });

        mDescriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextClick(DESCRIPTION_EDIT);
            }
        });

        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

    }

    private void onTextClick(final String field) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DescriptionActivity.this);
        alertDialogBuilder.setTitle(R.string.alert_title);

        final EditText input = new EditText(this);
        if (field == NAME_EDIT) {
            input.setText(mNameView.getText());
        }
        else if (field == DESCRIPTION_EDIT) {
            input.setText(mDescriptionView.getText());
        }
        alertDialogBuilder.setView(input);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (field == NAME_EDIT) {
                                    mNameView.setText(input.getText());
                                }
                                else if (field == DESCRIPTION_EDIT) {
                                    mDescriptionView.setText(input.getText());
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateView.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void completeData(Intent intent) {
        String taskName = intent.getStringExtra(MainActivity.EXTRA_TASK_NAME);
        String taskDescription = intent.getStringExtra(MainActivity.EXTRA_TASK_DESCRIPTION);
        String taskDate = intent.getStringExtra(MainActivity.EXTRA_TASK_DATE);

        oldTaskName = taskName;
        mNameView.setText(taskName);
        mDescriptionView.setText(taskDescription);
        mDateView.setText(taskDate);
    }

    public void doneClicked(View view) {

        String taskName = mNameView.getText().toString();
        String taskDescription = mDescriptionView.getText().toString();
        String taskDate = mDateView.getText().toString();

        if (!taskName.isEmpty()) {
            Intent taskIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(EXTRA_TASK_NAME, taskName);
            extras.putString(EXTRA_TASK_DESCRIPTION, taskDescription);
            extras.putString(EXTRA_TASK_DATE, taskDate);
            extras.putString(EXTRA_TASK_OLDNAME, oldTaskName);
            extras.putBoolean(EXTRA_TASK_DELETE, false);
            taskIntent.putExtras(extras);
            setResult(RESULT_OK, taskIntent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.input_name), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteClicked(View view) {
        Intent taskIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString(EXTRA_TASK_OLDNAME, oldTaskName);
        extras.putBoolean(EXTRA_TASK_DELETE, true);
        taskIntent.putExtras(extras);
        setResult(RESULT_OK, taskIntent);
        finish();
    }





}
