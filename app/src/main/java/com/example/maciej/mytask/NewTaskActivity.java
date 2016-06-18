package com.example.maciej.mytask;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_TASK_NAME = "task_name";
    public static final String EXTRA_TASK_DESCRIPTION = "task_desc";
    public static final String EXTRA_TASK_DATE = "date";

    private EditText mNameView;
    private EditText mDescriptionView;
    private EditText mDateView;
    private DatePickerDialog mDatePickerDialog;

    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        mNameView = (EditText) findViewById(R.id.nameText);
        mDescriptionView = (EditText) findViewById(R.id.descriptionText);
        mDateView = (EditText) findViewById(R.id.dateText);
        mDateView.setInputType(InputType.TYPE_NULL);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        
        setDateTimeField();
    }

    private void setDateTimeField() {
        mDateView.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mDateView.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == mDateView) {
            mDatePickerDialog.show();
        }
    }

    public void doneClicked(View view) {

        String taskName = mNameView.getText().toString();

        String taskDescription = mDescriptionView.getText().toString();
        if (taskDescription == null) {
            taskDescription = "";
        }

        String taskDate = mDateView.getText().toString();
        if (taskDate == null) {
            taskDate = "";
        }

        if (!taskName.isEmpty()) {
            Intent taskIntent = new Intent();
            Bundle extras = new Bundle();
            extras.putString(EXTRA_TASK_NAME, taskName);
            extras.putString(EXTRA_TASK_DESCRIPTION, taskDescription);
            extras.putString(EXTRA_TASK_DATE, taskDate);
            taskIntent.putExtras(extras);
            setResult(RESULT_OK, taskIntent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "input name", Toast.LENGTH_SHORT).show();
        }


    }

}
