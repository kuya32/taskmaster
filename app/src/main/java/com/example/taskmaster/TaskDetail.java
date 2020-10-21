package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class TaskDetail extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView taskTitle = findViewById(R.id.taskDetailTitle);
        taskTitle.setText(preferences.getString("doLab", "Name of Task"));
        taskTitle.setText(preferences.getString("doChallenge", "Name of Task"));
        taskTitle.setText(preferences.getString("doReading", "Name of Task"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}