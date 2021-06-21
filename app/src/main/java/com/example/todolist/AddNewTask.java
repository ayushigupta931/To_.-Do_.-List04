package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.todolist.db.AppDatabase;
import com.example.todolist.db.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import javax.microedition.khronos.egl.EGLDisplay;

public class AddNewTask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private FloatingActionButton save;
    private ImageView calender;
    private ImageView clock;
    private EditText time;
    private int hour, minute;
    private EditText date;
    private EditText yourTask;
    private Bundle bundle;
    private int Status = 0;
    private int taskId;
    private Tasks task1;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);

        save = (FloatingActionButton) findViewById(R.id.save);
        yourTask = (EditText) findViewById(R.id.newTaskField);
        date = (EditText) findViewById(R.id.DateField);
        time = (EditText) findViewById(R.id.TimeField);
        clock = (ImageView) findViewById(R.id.clock);
        calender = (ImageView) findViewById(R.id.calender);

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new com.example.todolist.DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        db = AppDatabase.getDatabase(this.getApplicationContext());

        save.setEnabled(false);
        bundle = getIntent().getExtras();
        if (bundle != null) {

            taskId = bundle.getInt("task_id");
            task1 = db.tasksDao().getTaskById(taskId);
            yourTask.setText(task1.task);
            String dateMtime = task1.date;
            if (!(dateMtime.equals(""))) {
                String[] x = dateMtime.split(",");

                date.setText(x[0] + "," + x[1]);
                if (x.length == 3)
                    time.setText(x[2]);


            }

            save.setEnabled(true);
        }


        yourTask.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (yourTask.getText().length() == 0)
                    save.setEnabled(false);
                else
                    save.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (date.getText().toString().equals("") && !(time.getText().toString().equals("")))
                    Toast.makeText(AddNewTask.this, "Set due date!", Toast.LENGTH_SHORT).show();
                else
                    saveTask();

            }
        });


    }

    public void saveTask() {

        String dateNTime;
        String s = yourTask.getText().toString();
        String d = date.getText().toString();
        String t = time.getText().toString();
        if (time.getText().toString().equals(""))
            dateNTime = d;
        else
            dateNTime = d + "," + t;
        if (bundle == null) {
            Tasks tas_k = new Tasks();
            tas_k.task = s;
            tas_k.date = dateNTime;
            tas_k.status = Status;

            db.tasksDao().insertTask(tas_k);
        } else {
            task1.task = s;
            task1.date = dateNTime;
            db.tasksDao().updateTask(task1);
        }
        finish();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String setDate = DateFormat.getDateInstance(DateFormat.FULL).format(cal.getTime());
        date.setText(setDate);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        time.setText(hourOfDay + ":" + minute);
    }
}