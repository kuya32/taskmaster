package com.macode.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskListener{

    Database database;

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        TextView homepageTitle = findViewById(R.id.homepageTitle);
        homepageTitle.setText(preferences.getString("usernameTasks", "My Tasks"));

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "macode_task_master")
                .allowMainThreadQueries()
                .build();
        List<Task> tasks = database.taskDAO().getTasksSortedByRecent();

        RecyclerView recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(tasks, this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = Room.databaseBuilder(getApplicationContext(), Database.class, "macode_task_master")
                .allowMainThreadQueries()
                .build();
        List<Task> tasks = database.taskDAO().getTasksSortedByRecent();

        RecyclerView recyclerView = findViewById(R.id.taskListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TaskAdapter(tasks, this));

        Button addTaskButton = MainActivity.this.findViewById(R.id.addTaskMainButton);
        addTaskButton.setOnClickListener((view) -> {
            System.out.println("Going to add a task page.");
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
            MainActivity.this.startActivity(goToAddTaskIntent);
        });

        Button allTasksButton = MainActivity.this.findViewById(R.id.allTasksMainButton);
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
    }

    @Override
    public void taskListener(Task task) {
        Intent goToTaskDetailsIntent = new Intent(MainActivity.this, TaskDetail.class);
        goToTaskDetailsIntent.putExtra("title", task.getTitle());
        goToTaskDetailsIntent.putExtra("body", task.getBody());
        goToTaskDetailsIntent.putExtra("state", task.getState());
        this.startActivity(goToTaskDetailsIntent);
    }
}