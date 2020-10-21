package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView homepageTitle = findViewById(R.id.homepageTitle);
        homepageTitle.setText(preferences.getString("usernameTasks", "My Tasks"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskButton = MainActivity.this.findViewById(R.id.addTaskButton);
        addTaskButton.setOnClickListener((view) -> {
            System.out.println("Going to add a task page.");
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
            MainActivity.this.startActivity(goToAddTaskIntent);
        });

        Button allTasksButton = MainActivity.this.findViewById(R.id.allTasksButton);
        allTasksButton.setOnClickListener((view) -> {
            System.out.println("Going to all tasks page.");
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasks.class);
            MainActivity.this.startActivity(goToAllTasksIntent);
        });

        ImageButton userSettingsButton = MainActivity.this.findViewById(R.id.userSettingsButton);
        userSettingsButton.setOnClickListener((view) -> {
            System.out.println("Going to user settings page.");
            Intent goToUserSettingsIntent = new Intent(MainActivity.this, UserSetting.class);
            MainActivity.this.startActivity((goToUserSettingsIntent));
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = preferences.edit();

        Button viewDoCodeLabButton = MainActivity.this.findViewById(R.id.doCodeLab);
        viewDoCodeLabButton.setOnClickListener((view) -> {
            System.out.println("Going to task details page.");
            preferenceEditor.putString("doLab", viewDoCodeLabButton.getText().toString());
            preferenceEditor.apply();
            Intent goToTaskDetailIntent = new Intent(MainActivity.this, TaskDetail.class);
            MainActivity.this.startActivity((goToTaskDetailIntent));
        });

        Button viewDoChallengeButton = MainActivity.this.findViewById(R.id.doCodeChallenge);
        viewDoChallengeButton.setOnClickListener((view) -> {
            System.out.println("Going to task details page.");
            preferenceEditor.putString("doChallenge", viewDoChallengeButton.getText().toString());
            preferenceEditor.apply();
            Intent goToTaskDetailIntent2 = new Intent(MainActivity.this, TaskDetail.class);
            MainActivity.this.startActivity((goToTaskDetailIntent2));
        });

        Button viewDoReadingButton = MainActivity.this.findViewById(R.id.doReading);
        viewDoReadingButton.setOnClickListener((view) -> {
            System.out.println("Going to task details page.");
            preferenceEditor.putString("doReading", viewDoReadingButton.getText().toString());
            preferenceEditor.apply();
            Intent goToTaskDetailIntent3 = new Intent(MainActivity.this, TaskDetail.class);
            MainActivity.this.startActivity((goToTaskDetailIntent3));
        });
    }

}